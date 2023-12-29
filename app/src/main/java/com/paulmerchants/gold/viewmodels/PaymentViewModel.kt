package com.paulmerchants.gold.viewmodels

import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.PayAll
import com.paulmerchants.gold.model.newmodel.ReqCreateOrder
import com.paulmerchants.gold.model.newmodel.ReqPayAlInOnGo
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespCreateOrder
import com.paulmerchants.gold.model.newmodel.RespUpdatePaymentStatus
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
) : ViewModel() {
    private val TAG = this.javaClass.name
    val responseCreateOrder = MutableLiveData<RespCreateOrder?>()
    val tokenExpiredResp = MutableLiveData<RespCommon?>()
    val respPaymentUpdate = MutableLiveData<RespUpdatePaymentStatus?>()

    init {
        Log.d(TAG, ": init_$TAG")
    }

    fun createOrder(appSharedPref: AppSharedPref?, reqCreateOrder: ReqCreateOrder) =
        viewModelScope.launch {

            retrofitSetup.callApi(true, object : CallHandler<Response<*>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<*> {
                    return apiParams.createOrder(
                        "Bearer ${appSharedPref?.getStringValue(Constants.JWT_TOKEN).toString()}",
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
                        if (appSharedPref != null) {
                            getLogin2(appSharedPref)
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
        navController: NavController,
        appSharedPref: AppSharedPref?,
        status: String?,
        razorpay_payment_id: String,
        razorpay_order_id: String,
        razorpay_signature: String,
        custId: String,
        amount: Double?,
        contactCount: Int,
        companyName: String = "PAUL MERCHANTS",
        currency: String = "INR",
        description: String,
        account: String,
        isCustom: Boolean,
    ) = viewModelScope.launch {

        retrofitSetup.callApi(true, object : CallHandler<Response<*>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<*> {
                return apiParams.updatePaymentStatus(
                    "Bearer ${appSharedPref?.getStringValue(Constants.JWT_TOKEN).toString()}",
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
                        respSuccess?.message.showSnackBar()
                        respPaymentUpdate.value = respSuccess
                        navController.navigateUp()
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
                    if (appSharedPref != null) {
                        getLogin2(appSharedPref)
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

    fun updatePaymentStatusAllInOneGo(
        navController: NavController,
        appSharedPref: AppSharedPref?,
        status: String?,
        razorpay_payment_id: String,
        razorpay_order_id: String,
        razorpay_signature: String,
        custId: String,
        amount: Double?,
        contactCount: Int,
        companyName: String = "PAUL MERCHANTS",
        currency: String = "INR",
        description: String,
        listOfPaullINOneGo: List<PayAll>,
    ) = viewModelScope.launch {

        retrofitSetup.callApi(true, object : CallHandler<Response<*>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<*> {
                return apiParams.updatePaymentStatusAllInOneGo(
                    "Bearer ${appSharedPref?.getStringValue(Constants.JWT_TOKEN).toString()}",
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
                        navController.navigateUp()
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
                    if (appSharedPref != null) {
                        getLogin2(appSharedPref)
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

    fun getLogin2(appSharedPref: AppSharedPref?) = viewModelScope.launch {
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
                    appSharedPref?.putStringValue(
                        Constants.AUTH_STATUS,
                        it
                    )
                }
                response.body()?.token?.let {
                    appSharedPref?.putStringValue(
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