package com.paulmerchants.gold.viewmodels

import android.app.Activity
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
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
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
import com.paulmerchants.gold.utility.showCustomDialogFoPaymentError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {
    private val TAG = this.javaClass.name

    var isCalled = true
    val responseCreateOrder = MutableLiveData<RespCreateOrder?>()
    val tokenExpiredResp = MutableLiveData<RespCommon?>()
    val respPaymentUpdate = MutableLiveData<RespUpdatePaymentStatus?>()
    val getPaymentMethod = MutableLiveData<RespPaymentMethod?>()
    val getRespCustomersDetailsLiveData = MutableLiveData<RespCustomCustomerDetail>()
    val isUnderMainLiveData = MutableLiveData<RespUnderMain>()
    val isRemoteConfigCheck = MutableLiveData<Boolean>()
    var remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        Log.d(TAG, ": init_$TAG")
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 30
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    fun checkForDownFromRemoteConfig() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { it ->
            if (it.isSuccessful) {
                val updated = it.result
                Log.d("loadData", ": $updated")
                val data = remoteConfig.getBoolean("isAppDown")
                Log.d("loadData", "$data")
                isRemoteConfigCheck.value = data
            } else {
                Log.d("loadData", "Else:")
            }
        }
    }

    fun getUnderMaintenanceStatusCheck() = viewModelScope.launch {
        try {

            val response = apiParams.isUnderMaintenance()
            // Get the plain text response
            val plainTextResponse = response.string()

            // Do something with the plain text response
            Log.d("Response", plainTextResponse)

            val decryptData = decryptKey(
                BuildConfig.SECRET_KEY_UAT,
                plainTextResponse
            )
            println("decrypt-----$decryptData")
            val gson = Gson()
//           val  typeToken = object : TypeToken<BaseResponse<RespUnderMain>>() {}
//            val respPending = gson.fromJson<BaseResponse<DataDown>>(decryptData.toString(),typeToken.type)
            val respPending = gson.fromJson (decryptData.toString(),RespUnderMain::class.java)

            isUnderMainLiveData.value = respPending
            println("Str_To_Json------$respPending")


        } catch (e: Exception) {
            e.printStackTrace()
        }
        AppUtility.hideProgressBar()
    }
    fun getUnderMaintenanceStatus(reqCreateOrder: ReqCreateOrder, location: Location?) = viewModelScope.launch {
        try {

            val response = apiParams.isUnderMaintenance()
            // Get the plain text response
            val plainTextResponse = response.string()

            // Do something with the plain text response
            Log.d("Response", plainTextResponse)

            val decryptData = decryptKey(
                BuildConfig.SECRET_KEY_UAT,
                plainTextResponse
            )
            println("decrypt-----$decryptData")
            val gson = Gson()
//           val  typeToken = object : TypeToken<BaseResponse<RespUnderMain>>() {}
//            val respPending = gson.fromJson<BaseResponse<DataDown>>(decryptData.toString(),typeToken.type)
            val respPending = gson.fromJson (decryptData.toString(),RespUnderMain::class.java)
            if (respPending.status_code ==200) {
                if (respPending?.data?.down == false) {
                    createOrder(reqCreateOrder, location = location)
                } else  {
//                                    findNavController.navigate(R.id.loginScreenFrag)
                    isUnderMainLiveData.value = respPending
//                                    "App is under maintenance. Please try after some time".showSnackBarForPayment()
                }
            }


            println("Str_To_Json------$respPending")


        } catch (e: Exception) {
            e.printStackTrace()
        }
        AppUtility.hideProgressBar()
    }

    fun getCustomerDetails(location: Location?) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqpendingInterstDueNew(
                    AppSharedPref.getStringValue(Constants.CUSTOMER_ID).toString(),
                    AppUtility.getDeviceDetails(location)
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), encryptedString.toString())

                val response = apiParams.getCustomerDetails(
                    "Bearer ${
                        AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                    }", requestBody
                )
                // Get the plain text response
                val plainTextResponse = response.string()

                // Do something with the plain text response
                Log.d("Response", plainTextResponse.toString())

                val decryptData = decryptKey(
                    BuildConfig.SECRET_KEY_UAT,
                    plainTextResponse
                )
                println("decrypt-----$decryptData")

//           val  typeToken = object : TypeToken<BaseResponse<RespUnderMain>>() {}
//            val respPending = gson.fromJson<BaseResponse<DataDown>>(decryptData.toString(),typeToken.type)
                val respPending = gson.fromJson(decryptData.toString(), RespGetCustomer::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {
                        AppSharedPref.putStringValue(
                            Constants.CUSTOMER_FULL_DATA,
                            decryptData.toString()
                        )
                        AppSharedPref.putStringValue(
                            Constants.CUST_EMAIL,
                            it.data?.email.toString()
                        )
                        getRespCustomersDetailsLiveData.value =
                            RespCustomCustomerDetail(
                                it.data.api_response,
                                it.data?.email.toString()
                            )

                    } else {
                        "Some thing went wrong..".showSnackBar()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }
   /* fun getCustomerDetails1(appSharedPref: AppSharedPref, location: Location?) =
        viewModelScope.launch {
            retrofitSetup.callApi(true, object : CallHandler<Response<RespGetCustomer>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespGetCustomer> {
                    return apiParams.getCustomerDetails(
                        "Bearer ${appSharedPref.getStringValue(Constants.JWT_TOKEN).toString()}",
                        ReqpendingInterstDueNew(
                            AppSharedPref.getStringValue(Constants.CUSTOMER_ID).toString(),
                            AppUtility.getDeviceDetails(location)
                        )
                    )
                }

                override fun success(response: Response<RespGetCustomer>) {
                    try {
                        // Get the plain text response
                        if (response.isSuccessful) {
                            val plainTextResponse = response.body()?.data?.api_response

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
                                    getRespCustomersDetailsLiveData.value =
                                        RespCustomCustomerDetail(
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
        }*/

    fun getPaymentMethod(AppSharedPref: AppSharedPref) =
        viewModelScope.launch {

            retrofitSetup.callApi(false, object : CallHandler<Response<RespPaymentMethod>> {
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


    fun createOrder(reqCreateOrder: ReqCreateOrder, location: Location?) =

        viewModelScope.launch {
            try {
                val gson = Gson()
                val jsonString = gson.toJson(reqCreateOrder)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), encryptedString.toString())

                val response = apiParams.createOrder(
                    "Bearer ${
                        AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                    }", requestBody
                )
                // Get the plain text response
                val plainTextResponse = response.string()

                // Do something with the plain text response
                Log.d("Response", plainTextResponse.toString())

                val decryptData = decryptKey(
                    BuildConfig.SECRET_KEY_UAT,
                    plainTextResponse
                )
                println("decrypt-----$decryptData")

//           val  typeToken = object : TypeToken<BaseResponse<RespUnderMain>>() {}
//            val respPending = gson.fromJson<BaseResponse<DataDown>>(decryptData.toString(),typeToken.type)
                val respPending = gson.fromJson(decryptData.toString(), RespCreateOrder::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {
                        responseCreateOrder.value = respPending

                    }
                    else if (it.status_code == 401) {
                        Log.d("FAILED_401", "400000111111: ...............${respPending}")
                        val gson = Gson()
                        val respFail: RespCommon? = gson.fromJson(
                            gson.toJsonTree(respPending).asJsonObject,
                            RespCommon::class.java
                        )
                        tokenExpiredResp.value = respFail
//                        getLogin2(location = location)
                    }else {
                        "Some thing went wrong..".showSnackBar()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }


/* fun createOrder(reqCreateOrder: ReqCreateOrder, location: Location?) =
        viewModelScope.launch {

            retrofitSetup.callApi(false, object : CallHandler<Response<*>> {
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
//                        getLogin2(location = location)
                    }

                    AppUtility.hideProgressBar()
                }

                override fun error(message: String) {
                    super.error(message)
                    Log.d("TAG", "error: ......$message")
                }
            })

        }*/


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
        location: Location?,
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
                    deviceDetailsDTO = AppUtility.getDeviceDetails(location),
                    isCustom = isCustom
                )
            }

            override fun success(response: Response<*>) {
                Log.d("TAG", "success: ......${response.body()}")
                if (response.code() == 200) {
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

//                    getLogin2(location)

                } else {
                    val gson = Gson()
                    val respSuccess: RespUpdatePaymentStatus? = gson.fromJson(
                        gson.toJsonTree(response.body()).asJsonObject,
                        RespUpdatePaymentStatus::class.java
                    )
//                        respSuccess?.message.showSnackBar()
                    respPaymentUpdate.value = respSuccess
//                    "Some thing went wrong..try again later".showSnackBar()
                    activity.showCustomDialogFoPaymentError(
                        message = response.message(),
                        isClick = {

                        })
                }
                AppUtility.hideProgressBar()
            }

            override fun error(message: String) {
                super.error(message)
                activity.showCustomDialogFoPaymentError(
                    message = "Something wrong please try again later",
                    isClick = {

                    })
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
        location: Location?,
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
                    payAllInOnGo = ReqPayAlInOnGo(
                        AppUtility.getDeviceDetails(location = location),
                        listOfPaullINOneGo
                    )
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
//                        getLogin2(location)
                    }
                } else {
                    val gson = Gson()
                    val respFail: RespUpdatePaymentStatus? = gson.fromJson(
                        gson.toJsonTree(response.body()).asJsonObject,
                        RespUpdatePaymentStatus::class.java
                    )
                    respPaymentUpdate.value = respFail
//                    "Some thing went wrong..try again later".showSnackBar()
                }
                AppUtility.hideProgressBar()
            }

            override fun error(message: String) {
                super.error(message)
                Log.d("TAG", "error: ......$message")
            }
        })

    }




}