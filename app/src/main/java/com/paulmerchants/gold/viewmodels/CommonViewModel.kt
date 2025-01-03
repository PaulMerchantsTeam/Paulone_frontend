package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import callApiGeneric
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

import com.paulmerchants.gold.model.requestmodels.ReqPendingInterstDue
import com.paulmerchants.gold.model.requestmodels.ReqRefreshToken
import com.paulmerchants.gold.model.responsemodels.BaseResponse
import com.paulmerchants.gold.model.responsemodels.PendingInterestDuesResponseData
import com.paulmerchants.gold.model.responsemodels.RespCreateOrder
import com.paulmerchants.gold.model.responsemodels.RespDataDown
import com.paulmerchants.gold.model.responsemodels.RespOutstandingLoan
import com.paulmerchants.gold.model.responsemodels.RespPendingInterestDue
import com.paulmerchants.gold.model.responsemodels.RespRefreshToken
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants.CUSTOMER_ID
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val apiParams: ApiParams
) : ViewModel() {


    var dueLoanSelected: PendingInterestDuesResponseData? = null
    var timer: CountDownTimer? = null
    private var remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    var notZero: List<PendingInterestDuesResponseData>? = arrayListOf()

    val countNum = MutableLiveData<Long>()
    var isStartAnim = MutableLiveData<Boolean>()
    val getPendingInterestDuesLiveData = MutableLiveData<BaseResponse<RespPendingInterestDue>>()
    val getRespGetLoanOutStandingLiveData = MutableLiveData<BaseResponse<RespOutstandingLoan>>()
    val responseCreateOrder = MutableLiveData<BaseResponse<RespCreateOrder>?>()
    val isUnderMainLiveData = MutableLiveData<BaseResponse<RespDataDown>>()
    val refreshTokenLiveData = MutableLiveData<BaseResponse<RespRefreshToken>>()
    val isRemoteConfigCheck = MutableLiveData<Boolean>()


    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 30
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        checkForDownFromRemoteConfig()
    }


    fun getUnderMaintenanceStatus(context: Context) {
        callApiGeneric<RespDataDown>(
            request = "",
            progress = true,
            context = context,
            apiCall = { requestBody ->
                apiParams.isUnderMaintenance()
            },
            onSuccess = { data ->
                isUnderMainLiveData.postValue(data)
            },
            onClientError = { data ->
                when (data.status_code) {
                    400 -> {
                        data.message.showSnackBar()


                    }

                    401 -> {
                        data.message.showSnackBar()


                    }

                    else -> {
                        data.message.showSnackBar()

                    }
                }
            },
            onTokenExpired = { data ->


            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }
        )
    }

    fun refreshToken(context: Context) {
        val request = ReqRefreshToken(AppSharedPref.getStringValue(JWT_TOKEN))
        callApiGeneric<RespRefreshToken>(
            request = request,
            progress = false,
            context = context,
            apiCall = { requestBody ->
                apiParams.refreshToken(requestBody)
            },
            onSuccess = { data ->
                AppSharedPref.putStringValue(JWT_TOKEN, data.data?.token.toString())
                refreshTokenLiveData.postValue(data)
            },
            onClientError = { data ->
                when (data.status_code) {
                    400 -> {
                        data.message.showSnackBar()


                    }

                    401 -> {
                        data.message.showSnackBar()


                    }

                    else -> {

                    }
                }
            },
            onTokenExpired = { data ->


            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }
        )
    }


    fun getPendingInterestDues(location: Location?, context: Context) {
        val request = ReqPendingInterstDue(
            AppSharedPref.getStringValue(CUSTOMER_ID)
                .toString(),
            AppUtility.getDeviceDetails(location)
        )
        callApiGeneric<RespPendingInterestDue>(
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
            onClientError = { data ->
                when (data.status_code) {
                    400 -> {
                        data.message.showSnackBar()


                    }

                    401 -> {
                        data.message.showSnackBar()


                    }

                    else -> {
                        data.message.showSnackBar()
                    }
                }
            },
            onTokenExpired = { data ->


                getPendingInterestDuesLiveData.postValue(data)


            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }
        )
    }


    fun getLoanOutstanding(location: Location?, context: Context) {
        val request = ReqPendingInterstDue(
            AppSharedPref.getStringValue(CUSTOMER_ID)
                .toString(),
            AppUtility.getDeviceDetails(location)
        )
        callApiGeneric<RespOutstandingLoan>(
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
            onClientError = { data ->
                when (data.status_code) {
                    400 -> {
                        data.message.showSnackBar()


                    }

                    401 -> {
                        data.message.showSnackBar()


                    }

                    else -> {
                        data.message.showSnackBar()
                    }
                }
            },
            onTokenExpired = { data ->

                getRespGetLoanOutStandingLiveData.postValue(data)

            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }
        )
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

