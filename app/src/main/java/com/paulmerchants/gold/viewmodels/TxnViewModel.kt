package com.paulmerchants.gold.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.paulmerchants.gold.model.responsemodels.Transactions
import com.paulmerchants.gold.pagingdata.TxnPagingSource
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TxnViewModel @Inject constructor(

    private val apiParams: ApiParams,
) : ViewModel() {

    private val TAG = this.javaClass.name


    init {
        Log.d(TAG, ": init_$TAG")
    }


    fun getTxnHistory(
        status: Int,
    ): Flow<PagingData<Transactions>> {
        val pager = Pager(config = PagingConfig(10, enablePlaceholders = false)) {
            TxnPagingSource(
                status,
                apiParams,
                "Bearer ${AppSharedPref.getStringValue(JWT_TOKEN)}",
                AppSharedPref.getStringValue(Constants.CUSTOMER_ID).toString()
            )
        }.flow.cachedIn(viewModelScope)
        return pager
    }


}