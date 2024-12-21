package com.paulmerchants.gold.viewmodels


import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import callApiGeneric
import com.paulmerchants.gold.model.requestmodels.ReqDeviceDetailsDTO
import com.paulmerchants.gold.model.requestmodels.ReqResetForgetPin
import com.paulmerchants.gold.model.requestmodels.ReqResetPin
import com.paulmerchants.gold.model.responsemodels.BaseResponse
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.Constants.SESSION_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResetMpinViewModel @Inject constructor(

    private val apiParams: ApiParams,
) : ViewModel() {
    private val TAG = this.javaClass.name
    val responseResetPin = MutableLiveData<BaseResponse<Any>>()
    val responseResetForgetPin = MutableLiveData<BaseResponse<Any>>()


    init {
        Log.d(TAG, ": init_$TAG")
    }

    fun changeMpin(progress :Boolean = true,
        confirmMPin: String,
        newMPin: String,
        current_mpin: String,
        deviceDetailsDTO: ReqDeviceDetailsDTO,
        context: Context
    ) {
        val request = ReqResetPin(
            current_mpin = current_mpin,
            confirm_mpin = confirmMPin,
            mobile_no = AppSharedPref.getStringValue(
                Constants.CUST_MOBILE
            ).toString(),
            new_mpin = newMPin,
            device_details_dto = deviceDetailsDTO
        )
        callApiGeneric<Any>(
            request = request,
            progress = progress,
            context = context,
            apiCall = { requestBody ->
                apiParams.reSetMPin(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }",
                    requestBody
                )
            },
            onSuccess = { data ->
                responseResetPin.postValue(data)
                AppUtility.hideProgressBar()
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

                    else -> {
                        Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                    }
                }
            },
            onTokenExpired = { data ->
                responseResetPin.postValue(data)

            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }
        )
    }


    fun resetForgetMpin(
        confirmMPin: String,
        newMPin: String,
        deviceDetailsDTO: ReqDeviceDetailsDTO,
        context: Context
    ) {
        val request = ReqResetForgetPin(
            confirm_mpin = confirmMPin,
            mobile_no = AppSharedPref.getStringValue(
                Constants.CUST_MOBILE
            ).toString(),
            new_mpin = newMPin,
            deviceDetailsDTO
        )
        callApiGeneric<Any>(
            request = request,
            progress = true,
            context = context,
            apiCall = { requestBody ->
                apiParams.resetOrForgetMpin(
                    AppSharedPref.getStringValue(SESSION_ID).toString(),
                    requestBody
                )
            },
            onSuccess = { data ->
                responseResetForgetPin.postValue(data)
                AppUtility.hideProgressBar()
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

                    else -> {
                        Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")
                    }
                }
            },
            onTokenExpired = { data ->
                responseResetForgetPin.postValue(data)
            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }
        )
    }


}