package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.Constants.LOGIN_WITH_MPIN
import com.paulmerchants.gold.model.ReqCustomerOtpNew
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespCustomersDetails
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.model.ResponseGetOtp
import com.paulmerchants.gold.model.ResponseVerifyOtp
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.model.newmodel.ReqLoginWithMpin
import com.paulmerchants.gold.model.newmodel.ReqResetPin
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespCustomCustomerDetail
import com.paulmerchants.gold.model.newmodel.RespLoginWithMpin
import com.paulmerchants.gold.model.newmodel.RespTxnHistory
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.AUTH_STATUS
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
) : ViewModel() {

    val getTokenResp = MutableLiveData<Response<LoginNewResp>>()
    private val TAG = this.javaClass.name
    val txnHistoryData = MutableLiveData<RespTxnHistory>()
    val verifyOtp = MutableLiveData<ResponseVerifyOtp>()

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

    fun getOtp( mobileNum: String,location: Location?) =
        viewModelScope.launch {

            retrofitSetup.callApi(true, object : CallHandler<Response<ResponseGetOtp>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<ResponseGetOtp> {
                    return apiParams.getOtp(

                        "Bearer ${AppSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                        ReqCustomerNew(mobileNum, AppUtility.getDeviceDetails(location)),
                    )
                }

                override fun success(response: Response<ResponseGetOtp>) {
                    if (response.isSuccessful) {
                        timerStart()
                    } else {
                        "Some thing went wrong..".showSnackBar()
                    }
//                    val decryptData = decryptKey(
//                        BuildConfig.SECRET_KEY_GEN, response.body()?.data
//                    )


                }


                override fun error(message: String) {
                    super.error(message)
                    Log.d("TAG", "error: ......$message")
                }
            })
        }
    fun verifyOtp(AppSharedPref: AppSharedPref?, mobileNum: String, otp: String,location: Location?) =
        viewModelScope.launch {
            retrofitSetup.callApi(true, object : CallHandler<Response<ResponseVerifyOtp>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<ResponseVerifyOtp> {
                    return apiParams.verifyOtp(
                        "Bearer ${AppSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                        ReqCustomerOtpNew(mobileNum, otp, AppUtility.getDeviceDetails(location)),
                    )
                }

                override fun success(response: Response<ResponseVerifyOtp>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it.statusCode == "200") {
                                AppSharedPref?.putStringValue(Constants.CUST_MOBILE, mobileNum)
                                verifyOtp.value = response.body()

                            } else {
                                "${it.message}".showSnackBar()
                            }
                        }
                    }


                }


                override fun error(message: String) {
                    super.error(message)
                    Log.d("TAG", "error: ......$message")
                }
            })
        }
    fun loginWithMpin(
        navController: NavController,
        AppSharedPref: AppSharedPref?,
        reqLoginWithMpin: ReqLoginWithMpin,
        location: Location?
    ) =
        viewModelScope.launch {
            retrofitSetup.callApi(true, object : CallHandler<Response<RespLoginWithMpin>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespLoginWithMpin> {
                    return apiParams.loginWithMpin(
                        "Bearer ${AppSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                        reqLoginWithMpin
                    )
                }

                override fun success(response: Response<RespLoginWithMpin>) {
                    Log.d("TAG", "success: ..loginWithMpin....${response.body()}")
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == "200") {
                            if (response.body()?.data == true) {  //user exist with valid credential..
                                "${response.body()?.message}".showSnackBar()
                                navController.popBackStack(R.id.loginScreenFrag, true)
                                navController.navigate(R.id.homeScreenFrag)
                                AppSharedPref?.putBoolean(LOGIN_WITH_MPIN, true)
                            } else {
                                "${response.body()?.message}".showSnackBar()
                            }
                        } else {

                        }
                    } else if (response.code() == 401) {
                        getLogin2(AppSharedPref, location = location)
                    }

                }

                override fun error(message: String) {
                    super.error(message)
                    Log.d("TAG", "error: ......$message")
                }
            })

        }


    fun getLogin2(AppSharedPref: AppSharedPref?,location: Location?) = viewModelScope.launch {
        Log.d("TAG", "getLogin: //../........")
        retrofitSetup.callApi(true, object : CallHandler<Response<LoginNewResp>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<LoginNewResp> {
                return apiParams.getLogin(
                    LoginReqNew(
                        AppUtility.getDeviceDetails(location),
                        BuildConfig.PASSWORD,
                        BuildConfig.USERNAME
                    )
                )
            }

            override fun success(response: Response<LoginNewResp>) {
                Log.d("TAG", "success: ......$response")
                if (response.isSuccessful) {
                    response.body()?.statusCode?.let {
                        AppSharedPref?.putStringValue(
                            Constants.AUTH_STATUS,
                            it
                        )
                    }
                    response.body()?.token?.let { AppSharedPref?.putStringValue(JWT_TOKEN, it) }
                    getTokenResp.value = response
                } else {
                    Log.e(TAG, "success: .........")
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

    /**
     * FAILED_CASE
     * {
    "status": "SUCCESS",
    "statusCode": "200",
    "message": "Nope, Check Your MobileNo Or M-PIN are Incorrect!",
    "data": false,
    "response_message": "Request Processed Successfully"
    }
     */

}