package com.paulmerchants.gold.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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

    fun getLoanOutstanding(appSharedPref: AppSharedPref?) = viewModelScope.launch {

        retrofitSetup.callApi(true, object : CallHandler<Response<RespGetLOanOutStanding>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<RespGetLOanOutStanding> {
                return apiParams.getLoanOutstanding(
                    "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                    ReqpendingInterstDueNew(
                        appSharedPref?.getStringValue(Constants.CUSTOMER_ID).toString(),
                        AppUtility.getDeviceDetails()
                    )
                )
            }

            override fun success(response: Response<RespGetLOanOutStanding>) {
                try {
//                    // Get the plain text response
//                    val plainTextResponse = response.body()?.data
//                    // Do something with the plain text response
//                    if (plainTextResponse != null) {
//                        Log.d("Response", plainTextResponse)
//                        val decryptData = decryptKey(
//                            BuildConfig.SECRET_KEY_GEN, plainTextResponse
//                        )
//                        println("decrypt-----$decryptData")
//                        val respPending: RespGetLoanOutStanding? =
//                            AppUtility.convertStringToJson(decryptData.toString())
////                val respPending = AppUtility.stringToJsonGetPending(decryptData.toString())
//                        respPending?.let { resp ->
//                            getRespGetLoanOutStandingLiveData.value = resp
//                        }
//                        println("Str_To_Json------$respPending")
//                    }
                    if (response.body()?.statusCode == "200") {
                        getRespGetLoanOutStandingLiveData.value = response.body()?.data
                    } else {
                        "Some thing went wrong".showSnackBar()
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


}