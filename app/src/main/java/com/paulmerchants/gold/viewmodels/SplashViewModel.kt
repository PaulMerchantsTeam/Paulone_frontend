package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants.AUTH_STATUS
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val apiParams: ApiParams,
    private val retrofitSetup: RetrofitSetup,
) : ViewModel() {
    private val TAG = "SplashViewModel"

    //"pml", "FU510N@pro"
    init {
        Log.d(TAG, ": init_")
    }

    fun getLogin2() = viewModelScope.launch {
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
                response.body()?.statusCode?.let { AppSharedPref.putStringValue(AUTH_STATUS, it) }
                response.body()?.token?.let { AppSharedPref.putStringValue(JWT_TOKEN, it) }
                AppUtility.hideProgressBar()
            }

            override fun error(message: String) {
                super.error(message)
                Log.d("TAG", "error: ......$message")
                AppUtility.hideProgressBar()
            }
        })
    }

    /*
        fun getLogin2(context: Context) = viewModelScope.launch {
            try {
                val response = apiParams.getLogin(
                    RequestLogin(
                        BuildConfig.PASSWORD,
                        BuildConfig.USERNAME
                    )
                )
                // Get the plain text response
                val plainTextResponse = response.string()

                // Do something with the plain text response
                Log.d("Response", plainTextResponse)

                val decryptData = decryptKey(
                    BuildConfig.SECRET_KEY_GEN,
                    plainTextResponse
                )
                println("decrypt-----$decryptData")
                val respLogin: RespLogin? = AppUtility.convertStringToJson(decryptData.toString())
    //            val respLogin:RespLogin = AppUtility.stringToJson(decryptData.toString())
                println("Str_To_Json------$respLogin")
                if (respLogin != null) {
                    AppSharedPref.putStringValue(AUTH_STATUS, respLogin.Status.toString())
                    if (respLogin.Status == true) {
                        AppSharedPref.putStringValue(JWT_TOKEN, respLogin.JWToken.toString())
                    } else {
                        Toast.makeText(context, "Some thing went wrong.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()
        }
    */

    var counter = 0

    fun setValue() {
        if (counter >= 3) return else counter += 1
    }

}