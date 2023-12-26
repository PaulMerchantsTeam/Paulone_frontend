package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.Constants.LOGIN_WITH_MPIN
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespCustomersDetails
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.ReqLoginWithMpin
import com.paulmerchants.gold.model.newmodel.ReqResetPin
import com.paulmerchants.gold.model.newmodel.RespCommon
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

    init {
        Log.d(TAG, ": init_$TAG")
    }


    fun loginWithMpin(
        navController: NavController,
        appSharedPref: AppSharedPref?,
        reqLoginWithMpin: ReqLoginWithMpin,
    ) =
        viewModelScope.launch {
            retrofitSetup.callApi(true, object : CallHandler<Response<RespLoginWithMpin>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespLoginWithMpin> {
                    return apiParams.loginWithMpin(
                        "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
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
                                appSharedPref?.putBoolean(LOGIN_WITH_MPIN, true)
                            } else {
                                "${response.body()?.message}".showSnackBar()
                            }
                        } else {

                        }
                    } else if (response.code() == 401) {
                        getLogin2(appSharedPref)
                    }

                }

                override fun error(message: String) {
                    super.error(message)
                    Log.d("TAG", "error: ......$message")
                }
            })

        }


    fun getLogin2(appSharedPref: AppSharedPref?) = viewModelScope.launch {
        Log.d("TAG", "getLogin: //../........")
        retrofitSetup.callApi(true, object : CallHandler<Response<LoginNewResp>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<LoginNewResp> {
                return apiParams.getLogin(
                    LoginReqNew(
                        AppUtility.getDeviceDetails(),
                        BuildConfig.PASSWORD,
                        BuildConfig.USERNAME
                    )
                )
            }

            override fun success(response: Response<LoginNewResp>) {
                Log.d("TAG", "success: ......$response")
                if (response.isSuccessful) {
                    response.body()?.statusCode?.let {
                        appSharedPref?.putStringValue(
                            Constants.AUTH_STATUS,
                            it
                        )
                    }
                    response.body()?.token?.let { appSharedPref?.putStringValue(JWT_TOKEN, it) }
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