package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.paulmerchants.gold.model.newmodel.PmlBranch
import com.paulmerchants.gold.model.newmodel.RespAllBranch
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.pagingdata.LocationPagingSource
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {
    private val TAG = this.javaClass.name
    val branchLocation = MutableLiveData<Response<RespAllBranch>>()

    init {
        Log.d(TAG, ": init_$TAG")
    }

    fun getBranchWithPaging(
        appSharedPref: AppSharedPref,
    ): Flow<PagingData<PmlBranch>> {
        val pager = Pager(config = PagingConfig(10, enablePlaceholders = false)) {
            LocationPagingSource(
                apiParams,
                "Bearer ${appSharedPref.getStringValue(com.paulmerchants.gold.utility.Constants.JWT_TOKEN)}"
            )
        }.flow.cachedIn(viewModelScope)
        return pager
    }

//    fun getBranchLocation(appSharedPref: AppSharedPref?) =
//        viewModelScope.launch {
//            Log.d("TAG", "getLogin: //../........")
//            retrofitSetup.callApi(true, object : CallHandler<Response<RespAllBranch>> {
//                override suspend fun sendRequest(apiParams: ApiParams): Response<RespAllBranch> {
//                    return apiParams.fetchAllBranch(
//                        "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}"
//                    )
//                }
//
//                override fun success(response: Response<RespAllBranch>) {
//                    Log.d("TAG", "success: ......$response")
//                    branchLocation.value = response
//                    AppUtility.hideProgressBar()
//                }
//
//                override fun error(message: String) {
//                    super.error(message)
//                    Log.d("TAG", "error: ......$message")
//                    AppUtility.hideProgressBar()
//                }
//            })
//        }


}