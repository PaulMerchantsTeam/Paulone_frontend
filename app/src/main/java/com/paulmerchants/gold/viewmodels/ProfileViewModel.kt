package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.os.Bundle
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
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.model.newmodel.ReqResetPin
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.AUTH_STATUS
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
    val verifyOtp = MutableLiveData<ResponseGetOtp>()

    init {
        Log.d(TAG, ": init_$TAG")
    }

    val getRespCustomersDetailsLiveData = MutableLiveData<RespCustomersDetails>()

    fun getCustomerDetails(appSharedPref: AppSharedPref) = viewModelScope.launch {
        retrofitSetup.callApi(true, object : CallHandler<Response<RespCommon>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<RespCommon> {
                return apiParams.getCustomerDetails(
                    "Bearer ${appSharedPref.getStringValue(JWT_TOKEN).toString()}",
                    ReqpendingInterstDueNew(
                        appSharedPref.getStringValue(Constants.CUSTOMER_ID).toString(),
                        AppUtility.getDeviceDetails()
                    )
                )
            }

            override fun success(response: Response<RespCommon>) {
                try {
                    // Get the plain text response
                    if (response.isSuccessful) {
                        val plainTextResponse = response.body()?.data

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
                                getRespCustomersDetailsLiveData.value = resp
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
            retrofitSetup.callApi(true, object : CallHandler<Response<ResponseGetOtp>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<ResponseGetOtp> {
                    return apiParams.verifyOtp(
                        "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                        ReqCustomerOtpNew(mobileNum, otp, AppUtility.getDeviceDetails()),
                    )
                }

                override fun success(response: Response<ResponseGetOtp>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it.statusCode == "200") {
                                appSharedPref?.putStringValue(Constants.CUST_MOBILE, mobileNum)
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