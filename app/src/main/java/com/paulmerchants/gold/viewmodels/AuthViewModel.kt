package com.paulmerchants.gold.viewmodels

import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.paulmerchants.gold.model.ReqCustomerOtpNew
import com.paulmerchants.gold.model.ReqSetMPin
import com.paulmerchants.gold.model.usedModels.SetMPinData
import com.paulmerchants.gold.model.usedModels.BaseResponse
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.model.usedModels.RespGetOtp
import com.paulmerchants.gold.networks.callApiGeneric
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
    var isMPinSet = MutableLiveData<Boolean>()
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

    /*fun getOtp1(mobileNum: String, activity: Activity) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqCustomerNew(
                    mobileNum,
                    AppUtility.getDeviceDetails((activity as MainActivity).mLocation)
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val response = apiParams.getOtp(requestBody)
                // Get the plain text response
                val plainTextResponse = response.string()

                // Do something with the plain text response
                Log.d("Response", plainTextResponse)

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
                        isCustomerExist.postValue(true)
                        it.data.customer_response_list?.get(0)?.let { cust ->
                            AppSharedPref.putStringValue(
                                CUSTOMER_NAME,
                                cust.cust_name.toString()
                            )
                            AppSharedPref.putStringValue(
                                CUSTOMER_ID,
                                cust.cust_id.toString()
                            )
                        }
                        AppSharedPref.putStringValue(
                            SESSION_ID,
                            "Bearer ${it.data.session_id.toString()}"
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }*/

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
                isCustomerExist.postValue(true)
                data.data?.customer_response_list?.get(0)?.let { cust ->
                    AppSharedPref.putStringValue(CUSTOMER_NAME, cust.cust_name.toString())
                    AppSharedPref.putStringValue(CUSTOMER_ID, cust.cust_id.toString())
                }
                AppSharedPref.putStringValue(
                    SESSION_ID,
                    "Bearer ${data.data?.session_id.toString()}"
                )
            }, onClientError = { code, errorMessage ->
                when (code) {
                    400 ->
                    {
                        errorMessage.showSnackBar()

                        Log.d("TAG", "getOtp: Bad Request: $errorMessage")

                    }
                    401 ->{
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
            onUnexpectedError = {errorMessage->
                Log.d("TAG", "getOtp: Invalid Token: $errorMessage")

            },
            onError = { errorMessage ->
                Log.d("TAG", "getOtp: Invalid Token: $errorMessage")

            }
        )
    }


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


   /* fun verifyOtp1(mobileNum: String, otp: String, location: Location?) =
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
                    AppSharedPref.getStringValue(SESSION_ID),
                    requestBody
                )
                // Get the plain text response
                val plainTextResponse = response.string()

                // Do something with the plain text response
                Log.d("Response", plainTextResponse)

                val decryptData = decryptKey(
                    BuildConfig.SECRET_KEY_UAT,
                    plainTextResponse
                )
                println("decrypt-----$decryptData")
                val respPending = gson.fromJson(decryptData.toString(), ResponseGetOtp::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {
                        AppSharedPref.putStringValue(CUST_MOBILE, mobileNum)
                        isOtpVerify.postValue(true)
//                        verifyOtp.value = respPending

                    } else {
                        "${it.message}".showSnackBar()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }*/

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
        callApiGeneric<SetMPinData>(
            request = request,
            progress = true,
            context =context ,
            apiCall = { requestBody ->
                apiParams.setMPin(
                    AppSharedPref.getStringValue(SESSION_ID),
                    requestBody
                )
            },
            onSuccess = { data ->
                isMPinSet.postValue(true)

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



    /*fun setMpin1(
        confirmMPin: String,
        setUpMPin: String,
        email: String,
        location: Location?
    ) = viewModelScope.launch {
        try {
            val gson = Gson()
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
            val jsonString = gson.toJson(request)
            val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
            val requestBody =
                encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiParams.setMPin(
                AppSharedPref.getStringValue(SESSION_ID),
                requestBody
            )
            // Get the plain text response
            val plainTextResponse = response.string()

            // Do something with the plain text response
            Log.d("Response", plainTextResponse)

            val decryptData = decryptKey(
                BuildConfig.SECRET_KEY_UAT,
                plainTextResponse
            )
            println("decrypt-----$decryptData")
            val respPending = gson.fromJson(decryptData.toString(), RespSetMpin::class.java)
            println("Str_To_Json------$respPending")
            respPending?.let {
                if (it.status_code == 200) {

                    isMPinSet.postValue(true)


                } else {
                    it.error_message.showSnackBar()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        AppUtility.hideProgressBar()

    }*/
}