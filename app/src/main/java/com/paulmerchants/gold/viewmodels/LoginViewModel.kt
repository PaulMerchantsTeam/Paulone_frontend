package com.paulmerchants.gold.viewmodels

import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.Constants.LOGIN_WITH_MPIN
import com.paulmerchants.gold.model.ReqCustomerOtpNew
import com.paulmerchants.gold.model.usedModels.BaseResponse
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.model.newmodel.ReqLoginWithMpin
import com.paulmerchants.gold.model.usedModels.RespLoginData
import com.paulmerchants.gold.model.usedModels.RespGetOtp
import com.paulmerchants.gold.model.newmodel.RespLoginWithMpin
import com.paulmerchants.gold.model.newmodel.RespTxnHistory
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.networks.callApiGeneric
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.REFRESH_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {

    //    val getTokenResp = MutableLiveData<Response<LoginNewResp>>()
    private val TAG = this.javaClass.name
    val txnHistoryData = MutableLiveData<RespTxnHistory>()
    val verifyOtp = MutableLiveData<BaseResponse<RespGetOtp>>()
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
        val request = ReqCustomerNew(
            mobileNum,
            AppUtility.getDeviceDetails((activity as MainActivity).mLocation)
        )

        callApiGeneric<RespGetOtp>(
            request = request,
            progress = true,
            context = activity,
            apiCall = { requestBody -> apiParams.getOtp(requestBody) },
            onSuccess = { data ->
                timerStart()
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

                    498 -> {
                        Log.d("TAG", "getOtp: Invalid Token: $errorMessage")
                    }
                }
            },
            onServerError = { code, errorMessage ->
                Log.d("TAG", "getOtp: Invalid Token: $errorMessage")

            },
            onUnexpectedError = { errorMessage ->
                Log.d("TAG", "getOtp: Invalid Token: $errorMessage")

            },
            onError = { errorMessage ->
                Log.d("TAG", "getOtp: Invalid Token: $errorMessage")

            }
        )
    }

    /*fun getOtp(mobileNum: String, location: Location?,context: Context) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqCustomerNew(mobileNum, AppUtility.getDeviceDetails(location))
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val response = apiParams.getOtp(requestBody)
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
                val respPending = gson.fromJson(decryptData.toString(), ResponseGetOtp::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {
                        timerStart()
                        AppSharedPref.putStringValue(
                            Constants.SESSION_ID,
                            "Bearer ${it.data.session_id.toString()}"
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
    fun verifyOtp(mobileNum: String, otp: String, location: Location?,context: Context) {
        val request = ReqCustomerOtpNew(
            mobileNum,
            otp,
            AppUtility.getDeviceDetails(location = location)
        )
        callApiGeneric<RespGetOtp>(
            request = request,
            progress = true,
            context =context ,
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
                    400 ->
                    {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "verifyOtp: Bad Request: $errorMessage")

                    }
                    401 ->{
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
            onUnexpectedError = {errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            },
            onError = { errorMessage ->
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")


            }
        )

    }

    /*fun verifyOtp(mobileNum: String, otp: String, location: Location?, context: Context) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqCustomerOtpNew(
                    mobileNum,
                    otp,
                    AppUtility.getDeviceDetails(location = location)
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val response = apiParams.verifyOtp(
                    AppSharedPref.getStringValue(Constants.SESSION_ID),
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
                val respPending = gson.fromJson(decryptData.toString(), ResponseGetOtp::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {
                        AppSharedPref?.putStringValue(Constants.CUST_MOBILE, mobileNum)
                        verifyOtp.value = respPending

                    } else {
                        "${it.message}".showSnackBar()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }*/
    fun loginWithMpin(
        navController: NavController,
        AppSharedPref: AppSharedPref?,
        pin: String,
        context: Context

        ) =
        viewModelScope.launch {
            val request = ReqLoginWithMpin(
                mobile_no = com.paulmerchants.gold.security.sharedpref.AppSharedPref.getStringValue(
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

                    com.paulmerchants.gold.security.sharedpref.AppSharedPref?.putBoolean(
                        LOGIN_WITH_MPIN,
                        true
                    )
                    com.paulmerchants.gold.security.sharedpref.AppSharedPref?.putStringValue(
                        Constants.JWT_TOKEN,
                        data.data?.token.toString()
                    )
                    com.paulmerchants.gold.security.sharedpref.AppSharedPref?.putStringValue(
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


    fun loginWithMpin1(
        navController: NavController,
        AppSharedPref: AppSharedPref?,
        pin: String,

        ) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqLoginWithMpin(
                    mobile_no = com.paulmerchants.gold.security.sharedpref.AppSharedPref.getStringValue(
                        Constants.CUST_MOBILE
                    ).toString(), pin = pin
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val response = apiParams.loginWithMpin(requestBody)

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
                    gson.fromJson(decryptData.toString(), RespLoginWithMpin::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {

                        "${respPending?.message}".showSnackBar()
                        navController.popBackStack(R.id.loginScreenFrag, true)
                        navController.navigate(R.id.homeScreenFrag)
                        AppSharedPref?.putBoolean(LOGIN_WITH_MPIN, true)
                        AppSharedPref?.putStringValue(Constants.JWT_TOKEN, it.data.token.toString())
                        AppSharedPref?.putStringValue(
                            REFRESH_TOKEN,
                            it.data.refresh_token.toString()
                        )

                    } else {
                        "${it.message}".showSnackBar()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }


}