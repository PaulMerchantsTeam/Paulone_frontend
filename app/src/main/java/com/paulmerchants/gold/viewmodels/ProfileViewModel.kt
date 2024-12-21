package com.paulmerchants.gold.viewmodels

import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.paulmerchants.gold.model.requestmodels.ReqCustomerOtpNew
import com.paulmerchants.gold.model.responsemodels.BaseResponse
import com.paulmerchants.gold.model.requestmodels.ReqGetOtp
import com.paulmerchants.gold.model.requestmodels.ReqPendingInterstDue
import com.paulmerchants.gold.model.responsemodels.RespGetCustomer
import com.paulmerchants.gold.model.responsemodels.RespGetOtp

import  callApiGeneric
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.CUSTOMER_FULL_DATA
import com.paulmerchants.gold.utility.Constants.CUST_EMAIL
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(

    private val apiParams: ApiParams,
) : ViewModel() {
    companion object {
        const val TAG = "ProfileViewModel"
    }

    var isCalled: Boolean = true
    val verifyOtp = MutableLiveData<BaseResponse<RespGetOtp>>()
    val logoutLiveData = MutableLiveData<BaseResponse<Any>>()

    var timer: CountDownTimer? = null
    val countNum = MutableLiveData<Long>()
    val countStr = MutableLiveData<String>()

    init {
        Log.d(TAG, ": init_$TAG")
    }

    val getRespCustomersDetailsLiveData = MutableLiveData<BaseResponse<RespGetCustomer>>()

    fun timerStart(millis: Long = 120000L) {
        timer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val inSecond = millisUntilFinished / 1000
                val count = if (inSecond < 10) {
                    "00:0$inSecond"
                } else if (inSecond == 120L) {
                    "2:00"
                } else if (inSecond > 60L) {
                    if (inSecond - 60 < 10) {
                        "1:0${inSecond - 60}"
                    } else {
                        "1:${inSecond - 60}"
                    }
                } else if (inSecond < 60) {
                    "00:$inSecond"
                } else {
                    ""
                }
                Log.d("TAG", "hideAndShowOtpView: $count") // Didn't receive? 00:30
                countNum.postValue(millisUntilFinished / 1000)
                countStr.postValue(count)
            }

            override fun onFinish() {
                countNum.postValue(0)
                countStr.postValue("00")
            }
        }
        timer?.start()
    }

    fun getCustomerDetails(location: Location?, context: Context) {
        val request = ReqPendingInterstDue(
            AppSharedPref.getStringValue(Constants.CUSTOMER_ID).toString(),
            AppUtility.getDeviceDetails(location)
        )
        callApiGeneric<RespGetCustomer>(
            request = request,
            progress = false,
            context = context,
            apiCall = { requestBody ->
                apiParams.getCustomerDetails(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }", requestBody
                )
            },
            onSuccess = { data ->

                val jsonString = Gson().toJson(data.data)

                AppSharedPref.putStringValue(
                    CUSTOMER_FULL_DATA,
                    jsonString
                )
                AppSharedPref.putStringValue(
                    CUST_EMAIL,
                    data?.data?.email.toString()
                )
                getRespCustomersDetailsLiveData.postValue(
                    data
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

                    else -> {
                        Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                    }
                }
            },
            onTokenExpired = { data ->
                getRespCustomersDetailsLiveData.postValue(
                    data
                )

            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }
        )
    }


    fun logout(progress: Boolean = true, context: Context) {


        callApiGeneric<Any>(
            request = "",
            progress = progress,
            context = context,
            apiCall = { requestBody -> apiParams.logOut(
                "Bearer ${
                    AppSharedPref.getStringValue(JWT_TOKEN).toString()
                }"
            )},
            onSuccess = { data ->
                logoutLiveData.postValue(data)
                AppSharedPref.clearSharedPref()

            }, onClientError = { code, errorMessage ->
                when (code) {
                    400 -> {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "getOtp: Bad Request: $errorMessage")

                    }

                    401 -> {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "getOtp: Unauthorized: $errorMessage")

                    }

                    else -> {
                        Log.d("TAG", "getOtp: Invalid Token: $errorMessage")
                    }
                }
            },
            onTokenExpired = { data ->
                logoutLiveData.postValue(data)

            },
            onUnexpectedError = { errorMessage ->
                Log.d("TAG", "getOtp: Invalid Token: $errorMessage")

            }
        )
    }




    fun getOtp(mobileNum: String, activity: Activity) {
        val request = ReqGetOtp(
            mobileNum,
            AppUtility.getDeviceDetails((activity as MainActivity).mLocation)
        )

        callApiGeneric<RespGetOtp>(
            request = request,
            progress = true,
            context = activity,
            apiCall = { requestBody -> apiParams.getOtp(requestBody) },
            onSuccess = { data ->
//                timerStart()
                AppSharedPref.putStringValue(
                    Constants.SESSION_ID,
                    "Bearer ${data.data?.session_id.toString()}"
                )
            }, onClientError = { code, errorMessage ->
                when (code) {
                    400 -> {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "getOtp: Bad Request: $errorMessage")

                    }

                    401 -> {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "getOtp: Unauthorized: $errorMessage")

                    }

                    else -> {
                        Log.d("TAG", "getOtp: Invalid Token: $errorMessage")
                    }
                }
            },
            onTokenExpired = { data ->


            },
            onUnexpectedError = { errorMessage ->
                Log.d("TAG", "getOtp: Invalid Token: $errorMessage")

            }
        )
    }

    fun verifyOtp(mobileNum: String, otp: String, location: Location?, context: Context) {
        val request = ReqCustomerOtpNew(
            mobileNum,
            otp,
            AppUtility.getDeviceDetails(location = location)
        )
        callApiGeneric<RespGetOtp>(
            request = request,
            progress = true,
            context = context,
            apiCall = { requestBody ->
                apiParams.verifyOtp(
                    AppSharedPref.getStringValue(Constants.SESSION_ID),
                    requestBody
                )
            },
            onSuccess = { data ->

                AppSharedPref?.putStringValue(Constants.CUST_MOBILE, mobileNum)

                verifyOtp.postValue(data)

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

                    else -> {
                        Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                    }
                }
            },
            onTokenExpired = { data ->
                verifyOtp.postValue(data)
            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }
        )

    }


}