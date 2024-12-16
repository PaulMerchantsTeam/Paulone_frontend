package com.paulmerchants.gold.viewmodels

import android.app.Activity
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.ReqCustomerOtpNew
import com.paulmerchants.gold.model.ReqSetMPin
import com.paulmerchants.gold.model.RespSetMpin
import com.paulmerchants.gold.model.ResponseGetOtp
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants.CUSTOMER_ID
import com.paulmerchants.gold.utility.Constants.CUSTOMER_NAME
import com.paulmerchants.gold.utility.Constants.CUST_MOBILE
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.Constants.SESSION_ID
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {
    var isCalledApi = true

    //true initially when activity or fragmnet launch ..this is to handle the ui configuration changes...
    var isFrmLogout: Boolean? = false
    var isStartAnim = MutableLiveData<Boolean>()
    var isCustomerExist = MutableLiveData<Boolean>()
    var isOtpVerify = MutableLiveData<Boolean>()
    var isMPinSet = MutableLiveData<Boolean>()
    val verifyOtp = MutableLiveData<ResponseGetOtp>()
    var enteredMobileTemp: String = ""
    val getTokenResp = MutableLiveData<Response<LoginNewResp>>()
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

    fun getOtp(mobileNum: String, activity: Activity) =
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
                        isCustomerExist.postValue(true)
                        it.data.customer_response_list[0].let { cust ->
                            AppSharedPref.putStringValue(CUSTOMER_NAME,
                                cust.cust_name.toString()
                            )
                            AppSharedPref.putStringValue(CUSTOMER_ID,
                                cust.cust_id.toString()
                            )
                        }
                        AppSharedPref.putStringValue(SESSION_ID, "Bearer ${it.data.session_id.toString()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }


    /*fun verifyOtp(mobileNum: String, otp: String, location: Location?) =
        viewModelScope.launch {
            retrofitSetup.callApi(true, object : CallHandler<Response<ResponseGetOtp>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<ResponseGetOtp> {
                    return apiParams.verifyOtp1(

                        ReqCustomerOtpNew(
                            mobileNum,
                            otp,
                            AppUtility.getDeviceDetails(location = location)
                        ),
                    )
                }

                override fun success(response: Response<ResponseGetOtp>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it.status_code == 200) {
                                AppSharedPref.putStringValue(CUST_MOBILE, mobileNum)
                                isOtpVerify.postValue(true)
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
        }*/

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
                    AppSharedPref.getStringValue(SESSION_ID),
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
                        AppSharedPref.putStringValue(CUST_MOBILE, mobileNum)
                        isOtpVerify.postValue(true)
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

    fun setMpin(
        confirmMPin: String,
        setUpMPin: String,
        email: String,
        location: Location?,
    ) = viewModelScope.launch {
        try {
            val gson = Gson()
            val request =  ReqSetMPin(
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
                RequestBody.create("text/plain".toMediaTypeOrNull(), encryptedString.toString())

            val response = apiParams.setMPin(
                AppSharedPref.getStringValue(SESSION_ID),
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

    }

   /* fun setMpin(
        confirmMPin: String,
        setUpMPin: String,
        email: String,
        location: Location?,
    ) = viewModelScope.launch {

        retrofitSetup.callApi(true, object : CallHandler<Response<RespSetMpin>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<RespSetMpin> {
                return apiParams.setMPin(
                    "Bearer ${AppSharedPref.getStringValue(JWT_TOKEN).toString()}",
                    ReqSetMPin(
                        confirmMPin = confirmMPin,
                        emailId = email,
                        fullName = AppSharedPref.getStringValue(
                            CUSTOMER_NAME
                        ).toString(),
                        mobileNo = AppSharedPref.getStringValue(
                            CUST_MOBILE
                        ).toString(),
                        setUpMPin = setUpMPin,
                        termsAndCondition = true,
                        deviceDetailsDTO = AppUtility.getDeviceDetails(location = location)
                    ),
                )
            }

            override fun success(response: Response<RespSetMpin>) {
//                    val decryptData = decryptKey(
//                        BuildConfig.SECRET_KEY_GEN, response.body()?.data
//                    )
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.statusCode == "200") {
                            isMPinSet.postValue(true)
                        } else {
                            "${it.errorMessage}".showSnackBar()
                        }
                    }
                }


            }


            override fun error(message: String) {
                super.error(message)
                Log.d("TAG", "error: ......$message")
            }
        })
    }*/

    /*
        fun getCustomer(mobileNum: String, context: Context) = viewModelScope.launch {

            if (mobileNum == "9999988888") {
                isCustomerExist.postValue(true)
                return@launch
            }

            try {
                val encMobile =
                    SecureFiles().encryptKey(mobileNum, BuildConfig.SECRET_KEY_GEN).toString()

                appSharedPref.getStringValue(JWT_TOKEN)?.let {
                    val response = apiParams.getCustomer(it, encMobile)
                    val plainTextResponse = response.string()
                    Log.d("Response", plainTextResponse)

                    val decryptData = decryptKey(
                        BuildConfig.SECRET_KEY_GEN, plainTextResponse
                    )
                    println("decrypt-----$decryptData")

                    val respCutomer: RespGetCustomer? =
                        AppUtility.convertStringToJson(decryptData.toString())
    //                val respCutomer = AppUtility.stringToJsonCustomer(decryptData.toString())
                        //getting initially first customer
                        if (respCutomer != null) {
                            if (respCutomer[0].Status == true) {
                                isCustomerExist.postValue(true)
                                Log.d(
                                    "TAG", "getCustomer: -CustomerId---${respCutomer[0].Cust_ID.toString()}"
                                )
                                Log.d(
                                    "TAG", "getCustomer: -CustName---${respCutomer[0].CustName.toString()}"
                                )
                                appSharedPref.putStringValue(CUSTOMER_ID, respCutomer[0].Cust_ID.toString())
                                appSharedPref.putStringValue(
                                    CUSTOMER_NAME, respCutomer[0].CustName.toString()
                                )
                            } else {
                                Toast.makeText(
                                    context, "Error: Status = ${respCutomer[0].Status}", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    AppUtility.hideProgressBar()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TAG", "getCustomer: ........${e.message}")
            }
        }
    */

}