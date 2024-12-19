package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem
import com.paulmerchants.gold.model.usedModels.BaseResponse
import com.paulmerchants.gold.model.usedModels.GeOtStandingRespObj
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespGetLOanOutStanding
import com.paulmerchants.gold.model.newmodel.RespTxnHistory
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.networks.callApiGeneric
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
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class GoldLoanScreenViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {
    var isCalledGoldLoanScreen: Boolean = true
    val getRespGetLoanOutStandingLiveData = MutableLiveData<BaseResponse<GeOtStandingRespObj>>()
    var respGetLoanOutStanding = ArrayList<RespGetLoanOutStandingItem>()


    private val TAG = this.javaClass.name
    val txnHistoryData = MutableLiveData<RespTxnHistory>()

    init {
        Log.d(TAG, ": init_$TAG")
    }

    override fun onCleared() {
        super.onCleared()
        isCalledGoldLoanScreen = false
    }

    fun getLoanOutstanding(location: Location?, context: Context)  {
        val request = ReqpendingInterstDueNew(
            AppSharedPref.getStringValue(Constants.CUSTOMER_ID)
                .toString(),
            AppUtility.getDeviceDetails(location)
        )
        callApiGeneric<GeOtStandingRespObj>(
            request = request,
            progress = false,
            context = context,
            apiCall = { requestBody ->
                apiParams.getLoanOutstanding(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }",
                    requestBody
                )
            },
            onSuccess = { data ->
                getRespGetLoanOutStandingLiveData.postValue(data)


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
    fun getLoanOutstanding1(  location: Location?,context: Context) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqpendingInterstDueNew(
                    AppSharedPref.getStringValue(Constants.CUSTOMER_ID)
                        .toString(),
                    AppUtility.getDeviceDetails(location)
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    encryptedString.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val response = apiParams.getLoanOutstanding(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }",
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
                    gson.fromJson(decryptData.toString(), RespGetLOanOutStanding::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {


//                        getRespGetLoanOutStandingLiveData.value =
//                            respPending?.data

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