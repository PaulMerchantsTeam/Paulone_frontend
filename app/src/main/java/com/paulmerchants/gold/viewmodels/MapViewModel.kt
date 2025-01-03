package com.paulmerchants.gold.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.paulmerchants.gold.model.responsemodels.PmlBranch
import com.paulmerchants.gold.pagingdata.LocationPagingSource
import com.paulmerchants.gold.pagingdata.SearchLocationPagingSource
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(

    private val apiParams: ApiParams,
) : ViewModel() {
    private val TAG = this.javaClass.name


    init {
        Log.d(TAG, ": init_$TAG")
    }

    fun getBranchWithPaging(

    ): Flow<PagingData<PmlBranch>> {
        val pager = Pager(config = PagingConfig(10, enablePlaceholders = false)) {
            LocationPagingSource(
                apiParams,
                "Bearer ${AppSharedPref.getStringValue(com.paulmerchants.gold.utility.Constants.JWT_TOKEN)}"
            )
        }.flow.cachedIn(viewModelScope)
        return pager
    }

    fun searchBranchWithPaging(
        branchName: String,

        ): Flow<PagingData<PmlBranch>> {
        val pager = Pager(config = PagingConfig(10, enablePlaceholders = false)) {
            SearchLocationPagingSource(
                apiParams,
                branchName,
                "Bearer ${AppSharedPref.getStringValue(com.paulmerchants.gold.utility.Constants.JWT_TOKEN)}"
            )
        }.flow.cachedIn(viewModelScope)
        return pager
    }

}