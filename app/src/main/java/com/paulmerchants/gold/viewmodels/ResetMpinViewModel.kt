package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.ReqResetForgetPin
import com.paulmerchants.gold.model.newmodel.ReqResetPin
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespResetFogetMpin
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
class ResetMpinViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
) : ViewModel() {
    private val TAG = this.javaClass.name
    val responseResetPin = MutableLiveData<Response<RespCommon>>()
    val responseResetForgetPin = MutableLiveData<Response<RespResetFogetMpin>>()
    init {
        Log.d(TAG, ": init_$TAG")
    }
    fun changeMpin(appSharedPref: AppSharedPref?, reqResetPin: ReqResetPin) =
        viewModelScope.launch {
            Log.d("TAG", "getLogin: //../........")
            retrofitSetup.callApi(true, object : CallHandler<Response<RespCommon>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespCommon> {
                    return apiParams.reSetMPin(
                        "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                        reqResetPin
                    )
                }
                override fun success(response: Response<RespCommon>) {
                    Log.d("TAG", "success: ......$response")
                    responseResetPin.value = response
                    AppUtility.hideProgressBar()
                }

                override fun error(message: String) {
                    super.error(message)
                    Log.d("TAG", "error: ......$message")
                    AppUtility.hideProgressBar()
                }
            })
        }
    fun resetForgetMpin(appSharedPref: AppSharedPref?, reqResetPin: ReqResetForgetPin) =
        viewModelScope.launch {
            Log.d("TAG", "getLogin: //../........")
            retrofitSetup.callApi(true, object : CallHandler<Response<RespResetFogetMpin>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespResetFogetMpin> {
                    return apiParams.resetOrForgetMpin(
                        "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                        reqResetPin
                    )
                }


                override fun success(response: Response<RespResetFogetMpin>) {
                    Log.d("TAG", "success: ......$response")
                    responseResetForgetPin.value = response
                    AppUtility.hideProgressBar()
                }

                override fun error(message: String) {
                    super.error(message)
                    Log.d("TAG", "error: ......$message")
                    AppUtility.hideProgressBar()
                }
            })
        }


}