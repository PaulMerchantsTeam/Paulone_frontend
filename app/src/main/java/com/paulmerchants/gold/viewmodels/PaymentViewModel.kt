package com.paulmerchants.gold.viewmodels

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.RespCustomersDetails
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.PayAll
import com.paulmerchants.gold.model.newmodel.ReqCreateOrder
import com.paulmerchants.gold.model.newmodel.ReqPayAlInOnGo
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespCreateOrder
import com.paulmerchants.gold.model.newmodel.RespCustomCustomerDetail
import com.paulmerchants.gold.model.newmodel.RespGetCustomer
import com.paulmerchants.gold.model.newmodel.RespPaymentMethod
import com.paulmerchants.gold.model.newmodel.RespUnderMain
import com.paulmerchants.gold.model.newmodel.RespUpdatePaymentStatus
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.AppUtility.showSnackBarForPayment
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.IS_SHOW_TXN
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.showCustomDialogFoPaymentStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
) : ViewModel() {
    private val TAG = this.javaClass.name

    var isCalled = true
    val responseCreateOrder = MutableLiveData<RespCreateOrder?>()
    val tokenExpiredResp = MutableLiveData<RespCommon?>()
    val respPaymentUpdate = MutableLiveData<RespUpdatePaymentStatus?>()
    val getPaymentMethod = MutableLiveData<RespPaymentMethod?>()
    val getRespCustomersDetailsLiveData = MutableLiveData<RespCustomCustomerDetail>()
    val isUnderMainLiveData = MutableLiveData<RespUnderMain>()

    init {
        Log.d(TAG, ": init_$TAG")
    }

    fun getUnderMaintenanceStatusCheck() = viewModelScope.launch {
        retrofitSetup.callApi(
            true,
            object : CallHandler<Response<RespUnderMain>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespUnderMain> {
                    return apiParams.isUnderMaintenance()
                }

                override fun success(response: Response<RespUnderMain>) {
                    Log.d("TAG", "success: ......${response.body()}")
                    if (response.isSuccessful) {
                        isUnderMainLiveData.value = response.body()
                    }
                }
            })
    }

    fun getUnderMaintenanceStatus(reqCreateOrder: ReqCreateOrder) = viewModelScope.launch {
        retrofitSetup.callApi(
            true,
            object : CallHandler<Response<RespUnderMain>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespUnderMain> {
                    return apiParams.isUnderMaintenance()
                }

                override fun success(response: Response<RespUnderMain>) {
                    Log.d("TAG", "success: ......${response.body()}")
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == "200") {
                            if (response.body()?.data?.down == false) {
                                createOrder(reqCreateOrder)
                            } else {
                                "App is under maintenance. Please try after some time".showSnackBarForPayment()
                            }
                        }
                    }
                }
            })
    }

    fun getCustomerDetails(AppSharedPref: AppSharedPref) = viewModelScope.launch {
        retrofitSetup.callApi(true, object : CallHandler<Response<RespGetCustomer>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<RespGetCustomer> {
                return apiParams.getCustomerDetails(
                    "Bearer ${AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()}",
                    ReqpendingInterstDueNew(
                        AppSharedPref.getStringValue(Constants.CUSTOMER_ID).toString(),
                        AppUtility.getDeviceDetails()
                    )
                )
            }

            override fun success(response: Response<RespGetCustomer>) {
                try {
                    // Get the plain text response
                    if (response.isSuccessful) {
                        val plainTextResponse = response.body()?.data?.apiResponse

                        // Do something with the plain text response
                        if (plainTextResponse != null) {
                            Log.d("Response", plainTextResponse)
                            val decryptData = decryptKey(
                                BuildConfig.SECRET_KEY_GEN, plainTextResponse
                            )

                            println("decrypt-----$decryptData")
                            val respPending: RespCustomersDetails? =
                                AppUtility.convertStringToJson(decryptData.toString())
//                val respPending = AppUtility.stringToJsonGetPending(decryptData.toString())
                            respPending?.let { resp ->
                                AppSharedPref.putStringValue(
                                    Constants.CUSTOMER_FULL_DATA,
                                    decryptData.toString()
                                )
                                AppSharedPref.putStringValue(
                                    Constants.CUST_EMAIL,
                                    response.body()?.data?.email.toString()
                                )
                                getRespCustomersDetailsLiveData.value = RespCustomCustomerDetail(
                                    resp,
                                    response.body()?.data?.email.toString()
                                )
                            }
                            println("Str_To_Json------$respPending")
                        }


                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
                AppUtility.hideProgressBar()
            }

            override fun error(message: String) {
                super.error(message)
            }
        })
    }

    fun getPaymentMethod(AppSharedPref: AppSharedPref) =
        viewModelScope.launch {

            retrofitSetup.callApi(true, object : CallHandler<Response<RespPaymentMethod>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespPaymentMethod> {
                    return apiParams.getPaymentMethod(
                        "Bearer ${AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()}"
                    )
                }

                override fun success(response: Response<RespPaymentMethod>) {
                    Log.d("TAG", "success: ......${response.body()}")
                    if (response.isSuccessful) {
                        getPaymentMethod.value = response.body()
                    } else {
                        "Some thing went wrong".showSnackBar()
                    }
                }
            })
        }


    fun createOrder(reqCreateOrder: ReqCreateOrder) =
        viewModelScope.launch {

            retrofitSetup.callApi(true, object : CallHandler<Response<*>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<*> {
                    return apiParams.createOrder(
                        "Bearer ${AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()}",
                        reqCreateOrder
                    )
                }

                override fun success(response: Response<*>) {
                    Log.d("TAG", "success: ......${response.body()}")
                    if (response.isSuccessful) {
                        try {
                            val gson = Gson()
                            val respSuccess: RespCreateOrder? = gson.fromJson(
                                gson.toJsonTree(response.body()).asJsonObject,
                                RespCreateOrder::class.java
                            )
                            // Get the plain text response
                            val plainTextResponse = respSuccess?.data
                            // Do something with the plain text response
                            Log.d("TAG", "success: .,..$plainTextResponse.")
                            responseCreateOrder.value = respSuccess
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (response.code() == 401) {
                        Log.d("FAILED_401", "400000111111: ...............${response.body()}")
                        val gson = Gson()
                        val respFail: RespCommon? = gson.fromJson(
                            gson.toJsonTree(response.body()).asJsonObject,
                            RespCommon::class.java
                        )
                        tokenExpiredResp.value = respFail
                        if (AppSharedPref != null) {
                            getLogin2()
                        }
                    }

                    AppUtility.hideProgressBar()
                }

                override fun error(message: String) {
                    super.error(message)
                    Log.d("TAG", "error: ......$message")
                }
            })

        }


    fun updatePaymentStatus(
        activity: Activity,
        status: String?,
        razorpay_payment_id: String,
        razorpay_order_id: String,
        razorpay_signature: String,
        custId: String,
        amount: Double?,
        contactCount: Int,
        companyName: String = "PAUL MERCHANTS",
        currency: String = "INR",
        description: String = "desc_payment",
        account: String,
        isCustom: Boolean,
    ) = viewModelScope.launch {

        retrofitSetup.callApi(true, object : CallHandler<Response<*>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<*> {
                return apiParams.updatePaymentStatus(
                    "Bearer ${AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()}",
                    amount = amount,
                    contactCount = contactCount,
                    companyName = companyName,
                    currency = currency,
                    description = description,
                    status = status.toString(),
                    razorpayPaymentId = razorpay_payment_id,
                    razorpayOrderId = razorpay_order_id,
                    razorpaySignature = razorpay_signature,
                    custId = custId,
                    acNo = account,
                    makerId = "12545as",
                    macID = Build.ID,
                    deviceDetailsDTO = AppUtility.getDeviceDetails(),
                    isCustom = isCustom
                )
            }

            override fun success(response: Response<*>) {
                Log.d("TAG", "success: ......${response.body()}")
                if (response.isSuccessful) {
                    try {
                        val gson = Gson()
                        val respSuccess: RespUpdatePaymentStatus? = gson.fromJson(
                            gson.toJsonTree(response.body()).asJsonObject,
                            RespUpdatePaymentStatus::class.java
                        )
//                        respSuccess?.message.showSnackBar()
                        respPaymentUpdate.value = respSuccess
//                        navController.navigateUp()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 401) {
                    Log.d("FAILED_401", "400000111111: ...............${response.body()}")
                    val gson = Gson()
                    val respFail: RespCommon? = gson.fromJson(
                        gson.toJsonTree(response.body()).asJsonObject,
                        RespCommon::class.java
                    )
                    tokenExpiredResp.value = respFail

                    getLogin2()

                } else {
//                    "Some thing went wrong..try again later".showSnackBar()
                    activity.showCustomDialogFoPaymentStatus(
                        message = response.message(),
                        isClick = {

                        })
                }
                AppUtility.hideProgressBar()
            }

            override fun error(message: String) {
                super.error(message)
                Log.d("TAG", "error: ......$message")
            }
        })

    }

    fun updatePaymentStatusAllInOneGo(
        status: String?,
        razorpay_payment_id: String,
        razorpay_order_id: String,
        razorpay_signature: String,
        custId: String,
        amount: Double?,
        contactCount: Int,
        companyName: String = "PAUL MERCHANTS",
        currency: String = "INR",
        description: String = "descriptionPayment",
        listOfPaullINOneGo: List<PayAll>,
    ) = viewModelScope.launch {
        retrofitSetup.callApi(true, object : CallHandler<Response<*>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<*> {
                return apiParams.updatePaymentStatusAllInOneGo(
                    "Bearer ${AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()}",
                    amount = amount,
                    contactCount = contactCount,
                    companyName = companyName,
                    currency = currency,
                    description = description,
                    status = status.toString(),
                    razorpayPaymentId = razorpay_payment_id,
                    razorpayOrderId = razorpay_order_id,
                    razorpaySignature = razorpay_signature,
                    custId = custId,
                    makerId = "12545as",
                    macID = Build.ID,
                    payAllInOnGo = ReqPayAlInOnGo(AppUtility.getDeviceDetails(), listOfPaullINOneGo)
                )
            }

            override fun success(response: Response<*>) {
                Log.d("TAG", "success: ......${response.body()}")
                if (response.isSuccessful) {
                    try {
                        val gson = Gson()
                        val respSuccess: RespUpdatePaymentStatus? = gson.fromJson(
                            gson.toJsonTree(response.body()).asJsonObject,
                            RespUpdatePaymentStatus::class.java
                        )
                        respSuccess?.message.showSnackBar()
                        respPaymentUpdate.value = respSuccess
//                        navController.navigateUp()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 401) {
                    Log.d("FAILED_401", "400000111111: ...............${response.body()}")
                    val gson = Gson()
                    val respFail: RespCommon? = gson.fromJson(
                        gson.toJsonTree(response.body()).asJsonObject,
                        RespCommon::class.java
                    )
                    tokenExpiredResp.value = respFail
                    if (AppSharedPref != null) {
                        getLogin2()
                    }
                } else {
                    "Some thing went wrong..try again later".showSnackBar()
                }
                AppUtility.hideProgressBar()
            }

            override fun error(message: String) {
                super.error(message)
                Log.d("TAG", "error: ......$message")
            }
        })

    }

    fun getLogin2() = viewModelScope.launch {
        Log.d("TAG", "getLogin: //../........")
        retrofitSetup.callApi(true, object : CallHandler<Response<LoginNewResp>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<LoginNewResp> {
                return apiParams.getLogin(
                    LoginReqNew(
                        AppUtility.getDeviceDetails(),
                        BuildConfig.PASSWORD,
                        BuildConfig.USERNAME
                    )
                )
            }

            override fun success(response: Response<LoginNewResp>) {
                Log.d("TAG", "success: ......$response")
                response.body()?.statusCode?.let {
                    AppSharedPref.putStringValue(
                        Constants.AUTH_STATUS,
                        it
                    )
                }
                response.body()?.token?.let {
                    AppSharedPref.putStringValue(
                        Constants.JWT_TOKEN,
                        it
                    )
                }
                AppUtility.hideProgressBar()
            }

            override fun error(message: String) {
                super.error(message)
                Log.d("TAG", "error: ......$message")
                AppUtility.hideProgressBar()
            }
        })
    }


}