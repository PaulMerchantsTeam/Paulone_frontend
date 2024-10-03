package com.paulmerchants.gold.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.paulmerchants.gold.model.newmodel.RespPaidSingleReceipt
import com.paulmerchants.gold.model.newmodel.RespPayReceipt
import com.paulmerchants.gold.model.newmodel.RespTxnHistory
import com.paulmerchants.gold.model.newmodel.Transactions
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.pagingdata.TxnPagingSource
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TxnReceiptViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {

    private val TAG = this.javaClass.name
    val paidReceipt = MutableLiveData<RespPayReceipt>()

    init {
        Log.d(TAG, ": init_$TAG")
    }

    fun getPaidReceipt( paymentId: String) =
        viewModelScope.launch {
            retrofitSetup.callApi(true, object : CallHandler<Response<RespPayReceipt>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespPayReceipt> {
                    return apiParams.getPaidReceipt(
                        "Bearer ${AppSharedPref.getStringValue(JWT_TOKEN).toString()}",
                        paymentId
                    )
                }

                override fun success(response: Response<RespPayReceipt>) {
                    Log.d("TAG", "success: ..getTxnHistory....${response.body()}")
                    if (response.body()?.statusCode == "200") {
                        paidReceipt.value = response.body()
                    } else {
//                        "${response.body()?.message}".showSnackBar()
                    }


                }

                override fun error(message: String) {
                    super.error(message)
                    Log.d("TAG", "error: ......$message")
                }
            })

        }

}