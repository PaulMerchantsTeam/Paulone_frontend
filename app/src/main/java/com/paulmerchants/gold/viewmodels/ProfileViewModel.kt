package com.paulmerchants.gold.viewmodels

import android.app.Activity
import android.content.Context
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
import com.paulmerchants.gold.model.usedModels.BaseResponse
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespDataCustomer
import com.paulmerchants.gold.model.usedModels.RespGetOtp
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.networks.callApiGeneric
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.CUSTOMER_FULL_DATA
import com.paulmerchants.gold.utility.Constants.CUST_EMAIL
import com.paulmerchants.gold.utility.Constants.IS_LOGOUT
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {
    companion object {
        const val TAG = "ProfileViewModel"
    }

    var isCalled: Boolean = true
    val verifyOtp = MutableLiveData<BaseResponse<RespGetOtp>>()

    var timer: CountDownTimer? = null
    val countNum = MutableLiveData<Long>()
    val countStr = MutableLiveData<String>()

    init {
        Log.d(TAG, ": init_$TAG")
    }

    val getRespCustomersDetailsLiveData = MutableLiveData<BaseResponse<RespDataCustomer>>()

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
        val request = ReqpendingInterstDueNew(
            AppSharedPref.getStringValue(Constants.CUSTOMER_ID).toString(),
            AppUtility.getDeviceDetails(location)
        )
        callApiGeneric<RespDataCustomer>(
            request = request,
            progress = true,
            context = context,
            apiCall = { requestBody ->
                apiParams.getCustomerDetails(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }", requestBody
                )
            },
            onSuccess = { data ->

                val jsonString = Gson().toJson(data)
                println(jsonString)
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

    /*fun getCustomerDetails1(location: Location?) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqpendingInterstDueNew(
                    AppSharedPref.getStringValue(Constants.CUSTOMER_ID).toString(),
                    AppUtility.getDeviceDetails(location)
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val response = apiParams.getCustomerDetails(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }", requestBody
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

//           val  typeToken = object : TypeToken<BaseResponse<RespUnderMain>>() {}
//            val respPending = gson.fromJson<BaseResponse<DataDown>>(decryptData.toString(),typeToken.type)
                val respPending = gson.fromJson(decryptData.toString(), RespGetCustomer::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {
                        AppSharedPref.putStringValue(
                            CUSTOMER_FULL_DATA,
                            decryptData.toString()
                        )
                        AppSharedPref.putStringValue(
                            CUST_EMAIL,
                            respPending?.data?.email.toString()
                        )
//                        getRespCustomersDetailsLiveData.value = respPending

                    } else {
                        "Some thing went wrong..".showSnackBar()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }
*/
    fun logout(navController: NavController, context: Context) {


        callApiGeneric<Any>(
            request = "",
            progress = true,
            context = context,
            apiCall = { requestBody -> apiParams.logOut(
                "Bearer ${
                    AppSharedPref.getStringValue(JWT_TOKEN).toString()
                }"
            )},
            onSuccess = { data ->
                AppSharedPref.clearSharedPref()
                val bundle = Bundle().apply {
                    putBoolean(IS_LOGOUT, true)
                }
                navController.popBackStack(R.id.homeScreenFrag, true)
                navController.popBackStack(R.id.profileFrag, true)
                navController.navigate(R.id.phoenNumVerifiactionFragment, bundle)
                "${data?.message}".showSnackBar()
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
    fun logout(navController: NavController) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val response = apiParams.logOut(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }"
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

//           val  typeToken = object : TypeToken<BaseResponse<RespUnderMain>>() {}
//            val respPending = gson.fromJson<BaseResponse<DataDown>>(decryptData.toString(),typeToken.type)
                val respPending = gson.fromJson(decryptData.toString(), RespCommon::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {

                        AppSharedPref.clearSharedPref()
                        val bundle = Bundle().apply {
                            putBoolean(IS_LOGOUT, true)
                        }
                        navController.popBackStack(R.id.homeScreenFrag, true)
                        navController.popBackStack(R.id.profileFrag, true)
                        navController.navigate(R.id.phoenNumVerifiactionFragment, bundle)
                        "${it?.message}".showSnackBar()
                    } else {
                        "${it.message}".showSnackBar()
                    }


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

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
//    fun getOtp(mobileNum: String, location: Location? ) =
//        viewModelScope.launch {
//            try {
//                val gson = Gson()
//                val request = ReqCustomerNew(mobileNum, AppUtility.getDeviceDetails(location))
//                val jsonString = gson.toJson(request)
//                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
//                val requestBody =
//                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//
//                val response = apiParams.getOtp(requestBody)
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
//
//                val respPending = gson.fromJson(decryptData.toString(), ResponseGetOtp::class.java)
//                println("Str_To_Json------$respPending")
//                respPending?.let {
//                    if (it.status_code == 200) {
//                        timerStart()
//                        AppSharedPref.putStringValue(
//                            Constants.SESSION_ID,
//                            "Bearer ${it.data.session_id.toString()}"
//                        )
//                    } else {
//                        "Some thing went wrong..".showSnackBar()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            AppUtility.hideProgressBar()
//
//        }

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
    /*    fun verifyOtp(mobileNum: String, otp: String, location: Location? ) =
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

}