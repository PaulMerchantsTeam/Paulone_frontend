package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.location.Location
import android.os.CountDownTimer
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

import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespCreateOrder
import com.paulmerchants.gold.model.newmodel.RespGetLOanOutStanding
import com.paulmerchants.gold.model.newmodel.RespUpdatePaymentStatus
import com.paulmerchants.gold.model.newmodel.StatusPayment
import com.paulmerchants.gold.model.usedModels.DataDown
import com.paulmerchants.gold.model.usedModels.GeOtStandingRespObj
import com.paulmerchants.gold.model.usedModels.GepPendingRespObj
import com.paulmerchants.gold.model.usedModels.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.usedModels.ReqRefreshToken
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.networks.callApiGeneric
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants.CUSTOMER_ID
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {

    val paymentData = MutableLiveData<StatusPayment?>()
    var dueLoanSelected: GetPendingInrstDueRespItem? = null

    var notZero: List<GetPendingInrstDueRespItem>? = arrayListOf()

    val getPendingInterestDuesLiveData = MutableLiveData<BaseResponse<GepPendingRespObj>>()

    val tokenExpiredResp = MutableLiveData<RespCommon?>()
    val getRespGetLoanOutStandingLiveData = MutableLiveData<BaseResponse<GeOtStandingRespObj>>()



    var timer: CountDownTimer? = null
    val countNum = MutableLiveData<Long>()

    var isStartAnim = MutableLiveData<Boolean>()
    private var remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    val responseCreateOrder = MutableLiveData<RespCreateOrder?>()
    val isUnderMainLiveData = MutableLiveData<BaseResponse<DataDown>>()
    val isRemoteConfigCheck = MutableLiveData<Boolean>()


    val respPaymentUpdate = MutableLiveData<RespUpdatePaymentStatus?>()

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 30
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        checkForDownFromRemoteConfig()
    }


    fun getUnderMaintenanceStatus(context: Context) {
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

    fun refreshToken(context: Context) {
        val request = ReqRefreshToken("Bearer ${AppSharedPref.getStringValue(JWT_TOKEN)}")
        callApiGeneric<Any>(
            request = request,
            progress = true,
            context = context,
            apiCall = { requestBody ->
                apiParams.refreshToken(requestBody)
            },
            onSuccess = { data ->

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


    fun getPendingInterestDues(location: Location?, context: Context) {
        val request = ReqpendingInterstDueNew(
            AppSharedPref.getStringValue(CUSTOMER_ID)
                .toString(),
            AppUtility.getDeviceDetails(location)
        )
        callApiGeneric<GepPendingRespObj>(
            request = request,
            progress = false,
            context = context,
            apiCall = { requestBody ->
                apiParams.getPendingInterestDues(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }",
                    requestBody
                )
            },
            onSuccess = { data ->
                getPendingInterestDuesLiveData.postValue(data)

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

//    fun getPendingInterestDues1(location: Location?, context: Context) =
//        viewModelScope.launch {
//            try {
//                val gson = Gson()
//                val request = ReqpendingInterstDueNew(
//                    AppSharedPref.getStringValue(CUSTOMER_ID)
//                        .toString(),
//                    AppUtility.getDeviceDetails(location)
//                )
//                val jsonString = gson.toJson(request)
//                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
//                val requestBody =
//                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//
//                val response = apiParams.getPendingInterestDues(
//                    "Bearer ${
//                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
//                    }",
//                    requestBody
//                )
//                // Get the plain text response
//                val plainTextResponse = response.string()
//
//                // Do something with the plain text response
//                Log.d("Response", plainTextResponse.toString())
//
//                val decryptData = decryptKey(
//                    BuildConfig.SECRET_KEY_UAT,
//                    plainTextResponse
//                )
//                println("decrypt-----$decryptData")
//                val respPending =
//                    gson.fromJson(decryptData.toString(), GetPendingResponse::class.java)
//                println("Str_To_Json------$respPending")
//                respPending?.let {
//                    if (it.status_code == 200) {
//
//
//                        getPendingInterestDuesLiveData.value =
//                            respPending
//
//                    } else {
//                        "${it.message}".showSnackBar()
//                    }
//
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            AppUtility.hideProgressBar()
//
//        }


    fun getLoanOutstanding(location: Location?, context: Context) {
        val request = ReqpendingInterstDueNew(
            AppSharedPref.getStringValue(CUSTOMER_ID)
                .toString(),
            AppUtility.getDeviceDetails(location)
        )
        callApiGeneric<GeOtStandingRespObj>(
            request = request,
            progress = false,
            context = context,
            apiCall = { requestBody ->
                apiParams.getLoanOutstanding(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }",
                    requestBody
                )
            },
            onSuccess = { data ->
                getRespGetLoanOutStandingLiveData.postValue(data)


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

    fun getLoanOutstanding1(location: Location?, context: Context) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqpendingInterstDueNew(
                    AppSharedPref.getStringValue(CUSTOMER_ID)
                        .toString(),
                    AppUtility.getDeviceDetails(location)
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val response = apiParams.getLoanOutstanding(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
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
                    gson.fromJson(decryptData.toString(), RespGetLOanOutStanding::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {


//                        getRespGetLoanOutStandingLiveData.value =
//                            respPending?.data

                    } else {
                        "${it.message}".showSnackBar()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

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


}

