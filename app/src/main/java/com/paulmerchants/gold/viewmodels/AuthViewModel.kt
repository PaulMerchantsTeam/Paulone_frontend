package com.paulmerchants.gold.viewmodels

import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import callApiGeneric
import com.paulmerchants.gold.model.requestmodels.ReqCustomerOtpNew
import com.paulmerchants.gold.model.requestmodels.ReqGetOtp
import com.paulmerchants.gold.model.requestmodels.ReqSetMPin
import com.paulmerchants.gold.model.responsemodels.BaseResponse
import com.paulmerchants.gold.model.responsemodels.RespGetOtp
import com.paulmerchants.gold.model.responsemodels.RespSetMPinData

import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants.CUSTOMER_ID
import com.paulmerchants.gold.utility.Constants.CUSTOMER_NAME
import com.paulmerchants.gold.utility.Constants.CUST_MOBILE
import com.paulmerchants.gold.utility.Constants.SESSION_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiParams: ApiParams
) : ViewModel() {
    var isCalledApi = true

    //true initially when activity or fragment launch ..this is to handle the ui configuration changes...
    var isFrmLogout: Boolean? = false
    var isCustomerExist = MutableLiveData<Boolean>()
    var isOtpVerify = MutableLiveData<Boolean>()

    var MPinLivedata = MutableLiveData<BaseResponse<RespSetMPinData>>()

    val verifyOtp = MutableLiveData<BaseResponse<RespGetOtp>>()
    var enteredMobileTemp: String = ""
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
                isCustomerExist.postValue(true)
                data.data?.customer_response_list?.get(0)?.let { cust ->
                    AppSharedPref.putStringValue(CUSTOMER_NAME, cust.cust_name.toString())
                    AppSharedPref.putStringValue(CUSTOMER_ID, cust.cust_id.toString())
                }
                AppSharedPref.putStringValue(
                    SESSION_ID,
                    "Bearer ${data.data?.session_id.toString()}"
                )
            },
            onClientError = { code, errorMessage ->
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
                        Log.d("TAG", "getOtp:  : $errorMessage")
                    }

                }
            },
            onTokenExpired = { data ->

            },
            onUnexpectedError = { errorMessage ->
                Log.d("TAG", "getOtp: Invalid Token: $errorMessage")
            },

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
                    AppSharedPref.getStringValue(SESSION_ID),
                    requestBody
                )
            },
            onSuccess = { data ->
                AppSharedPref.putStringValue(CUST_MOBILE, mobileNum)
                isOtpVerify.postValue(true)
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
                        Log.d("TAG", "verifyOtp:  : $errorMessage")
                    }
                }
            },
            onTokenExpired = { data ->
                verifyOtp.postValue(data)
                data.message.showSnackBar()

            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
            },
        )
    }

    fun setMpin(
        confirmMPin: String,
        setUpMPin: String,
        email: String,
        location: Location?,
        context: Context
    ) {
        val request = ReqSetMPin(
            confirm_mpin = confirmMPin,
            email_id = email,
            full_name = AppSharedPref.getStringValue(
                CUSTOMER_NAME
            ).toString(),
            mobile_no = AppSharedPref.getStringValue(
                CUST_MOBILE
            ).toString(),
            set_up_mpin = setUpMPin,
            terms_and_condition = true,
            device_details_dto = AppUtility.getDeviceDetails(location = location)
        )
        callApiGeneric<RespSetMPinData>(
            request = request,
            progress = true,
            context = context,
            apiCall = { requestBody ->
                apiParams.setMPin(
                    AppSharedPref.getStringValue(SESSION_ID),
                    requestBody
                )
            },
            onSuccess = { data ->

                MPinLivedata.postValue(data)
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
                MPinLivedata.postValue(data)
                data.message.showSnackBar()
            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            },
        )

    }

}