package com.paulmerchants.gold.viewmodels

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.RespGetLoanOutStanding
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem
import com.paulmerchants.gold.model.newmodel.GeOtStandingRespObj
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespGetLOanOutStanding
import com.paulmerchants.gold.model.newmodel.RespTxnHistory
import com.paulmerchants.gold.model.newmodel.Transactions
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.pagingdata.TxnPagingSource
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GoldLoanScreenViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {
    var isCalledGoldLoanScreen: Boolean = true
    val getRespGetLoanOutStandingLiveData = MutableLiveData<GeOtStandingRespObj>()
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


    fun getLoanOutstanding(  location: Location?) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request =  ReqpendingInterstDueNew(
                    AppSharedPref.getStringValue(Constants.CUSTOMER_ID)
                        .toString(),
                    AppUtility.getDeviceDetails(location)
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), encryptedString.toString())

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
                val respPending = gson.fromJson(decryptData.toString(), RespGetLOanOutStanding::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {


                        getRespGetLoanOutStandingLiveData.value =
                            respPending?.data

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