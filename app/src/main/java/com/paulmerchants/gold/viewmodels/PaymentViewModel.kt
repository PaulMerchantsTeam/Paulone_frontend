package com.paulmerchants.gold.viewmodels

import android.app.Activity
import android.content.Context
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
import com.paulmerchants.gold.model.usedModels.BaseResponse
import com.paulmerchants.gold.model.usedModels.Data
import com.paulmerchants.gold.model.newmodel.PaymentDetail
import com.paulmerchants.gold.model.newmodel.PaymentMethod
import com.paulmerchants.gold.model.newmodel.ReqCreateOrder
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RequestMTransaction
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespCreateOrder
import com.paulmerchants.gold.model.newmodel.RespCustomCustomerDetail
import com.paulmerchants.gold.model.newmodel.RespDataCustomer
import com.paulmerchants.gold.model.newmodel.RespPaymentMethod
import com.paulmerchants.gold.model.newmodel.RespUpdatePaymentStatus
import com.paulmerchants.gold.model.usedModels.DataDown
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.networks.callApiGeneric
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
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {
    private val TAG = this.javaClass.name
    var isCalled = true
    val responseCreateOrder = MutableLiveData<BaseResponse<Data>?>()
    val tokenExpiredResp = MutableLiveData<RespCommon?>()
    val respPaymentUpdate = MutableLiveData<BaseResponse<PaymentDetail>?>()
    val getPaymentMethod = MutableLiveData<BaseResponse<List<PaymentMethod>>?>()
    val getRespCustomersDetailsLiveData = MutableLiveData<RespCustomCustomerDetail>()
    val isUnderMainLiveData = MutableLiveData<BaseResponse<DataDown>>()
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
    fun getUnderMaintenanceStatusCheck(context: Context) {
        callApiGeneric<DataDown>(
            request = "",
            progress = true,
            context = context,
            apiCall = { requestBody ->
                apiParams.isUnderMaintenance()
            },
            onSuccess = { data ->
                isUnderMainLiveData.postValue(data)
            },
            onClientError = { code, errorMessage ->
                when (code) {
                    400 -> {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "verifyOtp: Bad Request: $errorMessage")

                    }

                    401 -> {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "verifyOtp: Unauthorized: $errorMessage")

                    }

                    498 -> {
                        Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                    }
                }
            },
            onServerError = { code, errorMessage ->
                errorMessage.showSnackBar()

                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            },
            onError = { errorMessage ->
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


            }
        )
    }
    fun getUnderMaintenanceStatus(reqCreateOrder: ReqCreateOrder,context: Context) {
        callApiGeneric<DataDown>(
            request = "",
            progress = true,
            context = context,
            apiCall = { requestBody ->
                apiParams.isUnderMaintenance()
            },
            onSuccess = { data ->
                if (data?.data?.down == false) {
                    createOrder(reqCreateOrder,context)
                } else {
                    isUnderMainLiveData.postValue(data)
                }

            },
            onClientError = { code, errorMessage ->
                when (code) {
                    400 -> {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "verifyOtp: Bad Request: $errorMessage")

                    }

                    401 -> {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "verifyOtp: Unauthorized: $errorMessage")

                    }

                    498 -> {
                        Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                    }
                }
            },
            onServerError = { code, errorMessage ->
                errorMessage.showSnackBar()

                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            },
            onError = { errorMessage ->
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


            }
        )
    }
    fun getCustomerDetails(location: Location?, context: Context) {
        val request = ReqpendingInterstDueNew(
            AppSharedPref.getStringValue(Constants.CUSTOMER_ID).toString(),
            AppUtility.getDeviceDetails(location)
        )
        callApiGeneric<RespDataCustomer>(
            request = request,
            progress = true,
            context = context,
            apiCall = { requestBody ->
                apiParams.getCustomerDetails(
                    "Bearer ${
                        AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                    }", requestBody
                )
            },
            onSuccess = { data ->
                AppSharedPref.putStringValue(
                    Constants.CUSTOMER_FULL_DATA,
                    data.toString()
                )
                AppSharedPref.putStringValue(
                    Constants.CUST_EMAIL,
                    data?.data?.email.toString()
                )
                getRespCustomersDetailsLiveData.postValue(
                    RespCustomCustomerDetail(
                        data.data?.api_response,
                        data?.data?.email.toString()
                    )
                )


            },
            onClientError = { code, errorMessage ->
                when (code) {
                    400 -> {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "verifyOtp: Bad Request: $errorMessage")

                    }

                    401 -> {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "verifyOtp: Unauthorized: $errorMessage")

                    }

                    498 -> {
                        Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                    }
                }
            },
            onServerError = { code, errorMessage ->
                errorMessage.showSnackBar()

                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            },
            onError = { errorMessage ->
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


            }
        )
    }
    /*fun getCustomerDetails1(location: Location?, context: Context) =
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
                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())

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

        }*/

    fun getPaymentMethod(context:Context) =
        viewModelScope.launch {


            callApiGeneric<List<PaymentMethod>>(
                request = "",
                progress = true,
                context = context,
                apiCall = { requestBody ->
                    apiParams.getPaymentMethod(
                        "Bearer ${
                            AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                        }"
                    )
                },
                onSuccess = { data ->
                    getPaymentMethod.postValue(data)



                },
                onClientError = { code, errorMessage ->
                    when (code) {
                        400 -> {
                            errorMessage.showSnackBar()

                            Log.d("TAG", "verifyOtp: Bad Request: $errorMessage")

                        }

                        401 -> {
                            errorMessage.showSnackBar()

                            Log.d("TAG", "verifyOtp: Unauthorized: $errorMessage")

                        }

                        498 -> {
                            Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                        }
                    }
                },
                onServerError = { code, errorMessage ->
                    errorMessage.showSnackBar()

                    Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


                },
                onUnexpectedError = { errorMessage ->
                    errorMessage.showSnackBar()
                    Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

                },
                onError = { errorMessage ->
                    Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


                }
            )


        }
    fun getPaymentMethod1() =
        viewModelScope.launch {
            try {
                val gson = Gson()


                val response = apiParams.getPaymentMethod(
                    "Bearer ${
                        AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                    }",
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
                val respPending =
                    gson.fromJson(decryptData.toString(), RespPaymentMethod::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {
//                        getPaymentMethod.value = respPending


                    } else {
                        "Some thing went wrong..".showSnackBar()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }

    fun createOrder(reqCreateOrder: ReqCreateOrder,context:Context) =
        viewModelScope.launch {


            callApiGeneric<Data>(
                request = reqCreateOrder,
                progress = true,
                context = context,
                apiCall = { requestBody ->
                    apiParams.createOrder(
                        "Bearer ${
                            AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                        }",requestBody
                    )
                },
                onSuccess = { data ->
                    responseCreateOrder.postValue(data)



                },
                onClientError = { code, errorMessage ->
                    when (code) {
                        400 -> {
                            errorMessage.showSnackBar()

                            Log.d("TAG", "verifyOtp: Bad Request: $errorMessage")

                        }

                        401 -> {
                            errorMessage.showSnackBar()
//                            tokenExpiredResp.postValue() = respFail
                            Log.d("TAG", "verifyOtp: Unauthorized: $errorMessage")

                        }

                        498 -> {
                            Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                        }
                    }
                },
                onServerError = { code, errorMessage ->
                    errorMessage.showSnackBar()

                    Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


                },
                onUnexpectedError = { errorMessage ->
                    errorMessage.showSnackBar()
                    Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

                },
                onError = { errorMessage ->
                    Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


                }
            )


        }
    fun createOrder1(reqCreateOrder: ReqCreateOrder) =

        viewModelScope.launch {
            try {
                val gson = Gson()
                val jsonString = gson.toJson(reqCreateOrder)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())

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
//                        responseCreateOrder.value = respPending

                    } else if (it.status_code == 401) {
                        Log.d("FAILED_401", "400000111111: ...............${respPending}")
                        val gson = Gson()
                        val respFail: RespCommon? = gson.fromJson(
                            gson.toJsonTree(respPending).asJsonObject,
                            RespCommon::class.java
                        )
                        tokenExpiredResp.value = respFail
//                        getLogin2(location = location)
                    } else {
                        "Some thing went wrong..".showSnackBar()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }

    fun mTransaction(
        activity: Activity,
        status: String?,
        razorpay_payment_id: String,
        razorpay_order_id: String,
        razorpay_signature: String,
        custId: String,
        amount: Double?,
        currency: String = "INR",
        description: String = "desc_payment",
        account: String,
        location: Location?,
    ) =

        viewModelScope.launch {


                val request = RequestMTransaction(
                    ac_no = account,
                    amount = amount,
                    currency = currency,
                    cust_id = custId,
                    description = description,
                    mac_id = Build.ID,
                    maker_id = "12545as",
                    razorpay_order_id = razorpay_order_id,
                    razorpay_payment_id = razorpay_payment_id,
                    razorpay_signature = razorpay_signature,
                    status = status,
                    device_details_dto = AppUtility.getDeviceDetails(location)
                )

                callApiGeneric<PaymentDetail>(
                    request = request,
                    progress = true,
                    context = activity,
                    apiCall = { requestBody ->
                        apiParams.mTransaction(
                            "Bearer ${
                                AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                            }", requestBody
                        )
                    },
                    onSuccess = { data ->
                        respPaymentUpdate.postValue(data)
                        AppSharedPref.putStringValue(
                            Constants.PAYMENT_ID,
                            data.data?.payment_id.toString()
                        )


                    },
                    onClientError = { code, errorMessage ->
                        when (code) {
                            400 -> {
                                errorMessage.showSnackBar()
                                activity.showCustomDialogFoPaymentError(
                                    message = errorMessage,
                                    isClick = {

                                    })
                                Log.d("TAG", "verifyOtp: Bad Request: $errorMessage")

                            }

                            401 -> {
                                errorMessage.showSnackBar()
                                activity.showCustomDialogFoPaymentError(
                                    message = errorMessage,
                                    isClick = {

                                    })
                                Log.d("TAG", "verifyOtp: Unauthorized: $errorMessage")

                            }

                            498 -> {
                                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                            }
                        }
                    },
                    onServerError = { code, errorMessage ->
                        errorMessage.showSnackBar()

                        Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                        activity.showCustomDialogFoPaymentError(
                            message = errorMessage,
                            isClick = {

                            })

                    },
                    onUnexpectedError = { errorMessage ->
                        errorMessage.showSnackBar()
                        Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

                    },
                    onError = { errorMessage ->
                        Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


                    }
                )



        }

    fun mTransaction1(
        activity: Activity,
        status: String?,
        razorpay_payment_id: String,
        razorpay_order_id: String,
        razorpay_signature: String,
        custId: String,
        amount: Double?,
        currency: String = "INR",
        description: String = "desc_payment",
        account: String,
        location: Location?,
    ) =

        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = RequestMTransaction(
                    ac_no = account,
                    amount = amount,
                    currency = currency,
                    cust_id = custId,
                    description = description,
                    mac_id = Build.ID,
                    maker_id = "12545as",
                    razorpay_order_id = razorpay_order_id,
                    razorpay_payment_id = razorpay_payment_id,
                    razorpay_signature = razorpay_signature,
                    status = status,
                    device_details_dto = AppUtility.getDeviceDetails(location)
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val response = apiParams.mTransaction(
                    "Bearer ${
                        AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                    }",
                    requestBody
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
                val respPending =
                    gson.fromJson(decryptData.toString(), RespUpdatePaymentStatus::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {

//                        respPaymentUpdate.value = respPending
                        AppSharedPref.putStringValue(Constants.PAYMENT_ID, it.data.payment_id)

                    } else if (it.status_code == 401) {
//                        tokenExpiredResp.value = respPending // tokenExpiration work to be done here
//                    "Some thing went wrong..try again later".showSnackBar()
                        activity.showCustomDialogFoPaymentError(
                            message = respPending.message,
                            isClick = {

                            })

                    } else {
//                        respPaymentUpdate.value = respPending
                        activity.showCustomDialogFoPaymentError(
                            message = respPending.message,
                            isClick = {

                            })
                        "${it.message}".showSnackBar()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }

}