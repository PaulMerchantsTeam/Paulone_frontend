package com.paulmerchants.gold.viewmodels

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
import com.paulmerchants.gold.model.ResponseGetOtp
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.model.newmodel.ReqLoginWithMpin
import com.paulmerchants.gold.model.newmodel.RespLoginWithMpin
import com.paulmerchants.gold.model.newmodel.RespTxnHistory
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {

    val getTokenResp = MutableLiveData<Response<LoginNewResp>>()
    private val TAG = this.javaClass.name
    val txnHistoryData = MutableLiveData<RespTxnHistory>()
    val verifyOtp = MutableLiveData<ResponseGetOtp>()

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

    fun getOtp(mobileNum: String, location: Location?) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqCustomerNew(mobileNum, AppUtility.getDeviceDetails(location))
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), encryptedString.toString())

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

        }


    fun verifyOtp(mobileNum: String, otp: String, location: Location?) =
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
                    RequestBody.create("text/plain".toMediaTypeOrNull(), encryptedString.toString())

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

                        reqLoginWithMpin
                    )
                }

                override fun success(response: Response<RespLoginWithMpin>) {
                    Log.d("TAG", "success: ..loginWithMpin....${response.body()}")
                    if (response.isSuccessful) {
                        if (response.body()?.status_code == "200") {
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
//                        getLogin2(AppSharedPref, location = location)
                    }

                }

                override fun error(message: String) {
                    super.error(message)
                    Log.d("TAG", "error: ......$message")
                }
            })

        }


}