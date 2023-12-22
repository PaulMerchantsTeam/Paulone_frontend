package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespCustomersDetails
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.PmlBranch
import com.paulmerchants.gold.model.newmodel.ReqResetPin
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespTxnHistory
import com.paulmerchants.gold.model.newmodel.Transactions
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.pagingdata.LocationPagingSource
import com.paulmerchants.gold.pagingdata.TxnPagingSource
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.AUTH_STATUS
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TxnViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {

    private val TAG = this.javaClass.name
    val txnHistoryData = MutableLiveData<RespTxnHistory>()

    init {
        Log.d(TAG, ": init_$TAG")
    }


    fun getTxnHistory(
        appSharedPref: AppSharedPref?,
    ): Flow<PagingData<Transactions>> {
        val pager = Pager(config = PagingConfig(10, enablePlaceholders = false)) {
            TxnPagingSource(
                apiParams,
                "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN)}",
                appSharedPref?.getStringValue(Constants.CUSTOMER_ID).toString()
            )
        }.flow.cachedIn(viewModelScope)
        return pager
    }

    /*
        fun getTxnHistory(appSharedPref: AppSharedPref?) =
            viewModelScope.launch {
                retrofitSetup.callApi(true, object : CallHandler<Response<RespTxnHistory>> {
                    override suspend fun sendRequest(apiParams: ApiParams): Response<RespTxnHistory> {
                        return apiParams.txnHistory(
                            "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                            appSharedPref?.getStringValue(Constants.CUSTOMER_ID).toString()
                        )
                    }

                    override fun success(response: Response<RespTxnHistory>) {
                        Log.d("TAG", "success: ..getTxnHistory....${response.body()}")
                        if (response.body()?.statusCode == "200") {
                            txnHistoryData.value = response.body()
                        } else {
                            "${response.body()?.message}".showSnackBar()
                        }


                    }

                    override fun error(message: String) {
                        super.error(message)
                        Log.d("TAG", "error: ......$message")
                    }
                })

            }
    */

}