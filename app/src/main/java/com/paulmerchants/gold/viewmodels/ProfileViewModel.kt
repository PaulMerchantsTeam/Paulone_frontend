package com.paulmerchants.gold.viewmodels

import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.model.ReqCustomerOtpNew
import com.paulmerchants.gold.model.RespCustomersDetails
import com.paulmerchants.gold.model.ResponseGetOtp
import com.paulmerchants.gold.model.ResponseVerifyOtp
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespCustomCustomerDetail
import com.paulmerchants.gold.model.newmodel.RespGetCustomer
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.CUSTOMER_FULL_DATA
import com.paulmerchants.gold.utility.Constants.CUST_EMAIL
import com.paulmerchants.gold.utility.Constants.CUST_MOBILE
import com.paulmerchants.gold.utility.Constants.IS_LOGOUT
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {
    companion object{
        const val TAG = "ProfileViewModel"
    }
    var isCalled: Boolean = true
    val verifyOtp = MutableLiveData<ResponseGetOtp>()

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

    fun getCustomerDetails(location: Location?) = viewModelScope.launch {
        retrofitSetup.callApi(true, object : CallHandler<Response<RespGetCustomer>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<RespGetCustomer> {
                return apiParams.getCustomerDetails(
                    "Bearer ${AppSharedPref.getStringValue(JWT_TOKEN).toString()}",
                    ReqpendingInterstDueNew(
                        AppSharedPref.getStringValue(Constants.CUSTOMER_ID).toString(),
                        AppUtility.getDeviceDetails(location)
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
                                AppSharedPref.putStringValue(
                                    CUSTOMER_FULL_DATA,
                                    decryptData.toString()
                                )
                                AppSharedPref.putStringValue(
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

        })
    }


    fun logout(navController: NavController) =
        viewModelScope.launch {
            retrofitSetup.callApi(true, object : CallHandler<Response<RespCommon>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespCommon> {
                    return apiParams.logOut(
                        "Bearer ${AppSharedPref.getStringValue(JWT_TOKEN).toString()}"
                    )
                }

                override fun success(response: Response<RespCommon>) {
                    try {
                        // Get the plain text response
                        if (response.body()?.statusCode == "200") {
                            AppSharedPref.clearSharedPref()
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

            })
        }

    fun getOtp(mobileNum: String, location: Location?) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request =  ReqCustomerNew(mobileNum, AppUtility.getDeviceDetails(location))
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
                    plainTextResponse)
                println("decrypt-----$decryptData")

//           val  typeToken = object : TypeToken<BaseResponse<RespUnderMain>>() {}
//            val respPending = gson.fromJson<BaseResponse<DataDown>>(decryptData.toString(),typeToken.type)
                val respPending = gson.fromJson(decryptData.toString(), ResponseGetOtp::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if(it.status_code == 200){
                        timerStart()
                        AppSharedPref.putStringValue(Constants.SESSION_ID, "Bearer ${it.data.session_id.toString()}")
                    }
                    else{
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

}