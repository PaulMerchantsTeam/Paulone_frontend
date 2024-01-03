package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.model.ReqCustomerOtpNew
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespCustomersDetails
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.model.ResponseGetOtp
import com.paulmerchants.gold.model.ResponseVerifyOtp
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.model.newmodel.ReqResetPin
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespCustomCustomerDetail
import com.paulmerchants.gold.model.newmodel.RespGetCustomer
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.AUTH_STATUS
import com.paulmerchants.gold.utility.Constants.CUSTOMER_FULL_DATA
import com.paulmerchants.gold.utility.Constants.CUST_EMAIL
import com.paulmerchants.gold.utility.Constants.CUST_MOBILE
import com.paulmerchants.gold.utility.Constants.IS_LOGOUT
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
) : ViewModel() {
    var isCalled: Boolean = true
    private val TAG = this.javaClass.name
    val verifyOtp = MutableLiveData<ResponseVerifyOtp>()

    var timer: CountDownTimer? = null
    val countNum = MutableLiveData<Long>()
    val countStr = MutableLiveData<String>()

    init {
        Log.d(TAG, ": init_$TAG")
    }

    val getRespCustomersDetailsLiveData = MutableLiveData<RespCustomCustomerDetail>()

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

    fun getCustomerDetails(appSharedPref: AppSharedPref) = viewModelScope.launch {
        retrofitSetup.callApi(true, object : CallHandler<Response<RespGetCustomer>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<RespGetCustomer> {
                return apiParams.getCustomerDetails(
                    "Bearer ${appSharedPref.getStringValue(JWT_TOKEN).toString()}",
                    ReqpendingInterstDueNew(
                        appSharedPref.getStringValue(Constants.CUSTOMER_ID).toString(),
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
                                appSharedPref.putStringValue(
                                    CUSTOMER_FULL_DATA,
                                    decryptData.toString()
                                )
                                appSharedPref.putStringValue(
                                    CUST_EMAIL,
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

    // {"status":"SUCCESS" , "statusCode":"200","message": "User Has Been Logout Successfully!"}
    fun logout(navController: NavController, appSharedPref: AppSharedPref?) =
        viewModelScope.launch {
            retrofitSetup.callApi(true, object : CallHandler<Response<RespCommon>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespCommon> {
                    return apiParams.logOut(
                        "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}"
                    )
                }

                override fun success(response: Response<RespCommon>) {
                    try {
                        // Get the plain text response
                        if (response.body()?.statusCode == "200") {
                            appSharedPref?.clearSharedPref()
                            val backStack = navController.backQueue
                            for (i in backStack) {
                                Log.d(
                                    TAG,
                                    "success: ...${i.id}..--------.${i.destination.displayName}"
                                )
//                                i.destination.route?.let { navController.popBackStack(it, true) }
//                                navController.clearBackStack(i.id)
                            }
                            val bundle = Bundle().apply {
                                putBoolean(IS_LOGOUT, true)
                            }
                            navController.popBackStack(R.id.homeScreenFrag, true)
                            navController.popBackStack(R.id.profileFrag, true)
                            navController.navigate(R.id.phoenNumVerifiactionFragment, bundle)
                            "${response.body()?.message}".showSnackBar()
                        } else {
                            "${response.body()?.message}".showSnackBar()
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

    fun getOtp(appSharedPref: AppSharedPref?, mobileNum: String) =
        viewModelScope.launch {

            retrofitSetup.callApi(true, object : CallHandler<Response<ResponseGetOtp>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<ResponseGetOtp> {
                    return apiParams.getOtp(

                        "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                        ReqCustomerNew(mobileNum, AppUtility.getDeviceDetails()),
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

    fun verifyOtp(appSharedPref: AppSharedPref?, mobileNum: String, otp: String) =
        viewModelScope.launch {
            retrofitSetup.callApi(true, object : CallHandler<Response<ResponseVerifyOtp>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<ResponseVerifyOtp> {
                    return apiParams.verifyOtp(
                        "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                        ReqCustomerOtpNew(mobileNum, otp, AppUtility.getDeviceDetails()),
                    )
                }

                override fun success(response: Response<ResponseVerifyOtp>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it.statusCode == "200") {
                                appSharedPref?.putStringValue(CUST_MOBILE, mobileNum)
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
}