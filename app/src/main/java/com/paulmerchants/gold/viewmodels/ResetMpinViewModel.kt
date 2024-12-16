package com.paulmerchants.gold.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.newmodel.DeviceDetailsDTO
import com.paulmerchants.gold.model.newmodel.ReqResetForgetPin
import com.paulmerchants.gold.model.newmodel.ReqResetPin
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespResetFogetMpin
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
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
class ResetMpinViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {
    private val TAG = this.javaClass.name
    val responseResetPin = MutableLiveData< RespCommon> ()
    val responseResetForgetPin = MutableLiveData<RespResetFogetMpin>()


    init {
        Log.d(TAG, ": init_$TAG")
    }

    fun changeMpin(confirmMPin: String, newMPin: String,current_mpin:String, deviceDetailsDTO: DeviceDetailsDTO) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request =  ReqResetPin(
                    current_mpin = current_mpin,
                    confirm_mpin = confirmMPin,
                    mobile_no = AppSharedPref.getStringValue(
                        Constants.CUST_MOBILE
                    ).toString(),
                    new_mpin = newMPin,
                    device_details_dto = deviceDetailsDTO
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), encryptedString.toString())

                val response = apiParams.reSetMPin(
                    "Bearer ${AppSharedPref.getStringValue(Constants.JWT_TOKEN)}",
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
                val respPending =
                    gson.fromJson(decryptData.toString(), RespCommon::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {
                        responseResetPin.value = respPending
                        AppUtility.hideProgressBar()
                    } else {
                        "${it.message}".showSnackBar()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }


    fun resetForgetMpin(confirmMPin: String, newMPin: String, deviceDetailsDTO: DeviceDetailsDTO) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqResetForgetPin(
                    confirm_mpin = confirmMPin,
                    mobile_no = AppSharedPref.getStringValue(
                        Constants.CUST_MOBILE
                    ).toString(),
                    new_mpin = newMPin,
                    deviceDetailsDTO
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), encryptedString.toString())

                val response = apiParams.resetOrForgetMpin(
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
                val respPending =
                    gson.fromJson(decryptData.toString(), RespResetFogetMpin::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {
                    responseResetForgetPin.value = respPending
                        AppUtility.hideProgressBar()
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