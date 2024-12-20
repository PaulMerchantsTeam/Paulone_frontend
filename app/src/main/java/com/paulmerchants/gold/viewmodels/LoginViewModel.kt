package com.paulmerchants.gold.viewmodels

import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import callApiGeneric
import com.paulmerchants.gold.common.Constants.LOGIN_WITH_MPIN
import com.paulmerchants.gold.model.other.RespTxnHistory
import com.paulmerchants.gold.model.requestmodels.ReqCustomerOtpNew
import com.paulmerchants.gold.model.requestmodels.ReqGetOtp
import com.paulmerchants.gold.model.requestmodels.ReqLoginWithMpin
import com.paulmerchants.gold.model.responsemodels.BaseResponse
import com.paulmerchants.gold.model.responsemodels.RespGetOtp
import com.paulmerchants.gold.model.responsemodels.RespLoginData

import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.REFRESH_TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(

    private val apiParams: ApiParams
) : ViewModel() {
    private val TAG = this.javaClass.name
    val txnHistoryData = MutableLiveData<RespTxnHistory>()
    val verifyOtp = MutableLiveData<BaseResponse<RespGetOtp>>()
    val getOtpLiveData = MutableLiveData<BaseResponse<RespGetOtp>>()
    val loginWithMpinLiveData = MutableLiveData<BaseResponse<RespLoginData>>()

    init {
        Log.d(TAG, ": init_$TAG")
    }

    var timer: CountDownTimer? = null
    val countNum = MutableLiveData<Long>()
    val countStr = MutableLiveData<String>()

    fun timerStart(millis: Long = 120000L) {
        timer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var count = "${millisUntilFinished / 1000}"
                val inSecond = millisUntilFinished / 1000
                if (inSecond < 10) {
                    count = "00:0$inSecond"
                } else if (inSecond == 120L) {
                    count = "2:00"
                } else if (inSecond > 60L) {
                    count = if (inSecond - 60 < 10) {
                        "1:0${inSecond - 60}"
                    } else {
                        "1:${inSecond - 60}"
                    }
                } else if (inSecond < 60) {
                    count = "00:$inSecond"
                } else {
                    count = ""
                }
                Log.d("TAG", "hideAndShowOtpView: $count") //Didn’t receive? 00:30
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
                getOtpLiveData.postValue(data)
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
                        errorMessage.showSnackBar()

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
                        errorMessage.showSnackBar()
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


    fun loginWithMpin(
        pin: String,
        context: Context
    ) =
        viewModelScope.launch {
            val request = ReqLoginWithMpin(
                mobile_no = AppSharedPref.getStringValue(
                    Constants.CUST_MOBILE
                ).toString(), pin = pin
            )
            callApiGeneric<RespLoginData>(
                request = request,
                progress = true,
                context = context,
                apiCall = { requestBody ->
                    apiParams.loginWithMpin(
                        requestBody
                    )
                },
                onSuccess = { data ->
                    loginWithMpinLiveData.postValue(data)
                    "${data?.message}".showSnackBar()

                    AppSharedPref?.putBoolean(
                        LOGIN_WITH_MPIN,
                        true
                    )
                    AppSharedPref?.putStringValue(
                        Constants.JWT_TOKEN,
                        data.data?.token.toString()
                    )
                    AppSharedPref?.putStringValue(
                        REFRESH_TOKEN,
                        data.data?.refresh_token.toString()
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
                            errorMessage.showSnackBar()

                            Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                        }
                    }
                },
                onTokenExpired = { data ->
                    data.message.showSnackBar()


                },
                onUnexpectedError = { errorMessage ->
                    errorMessage.showSnackBar()
                    Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

                }
            )
        }


}