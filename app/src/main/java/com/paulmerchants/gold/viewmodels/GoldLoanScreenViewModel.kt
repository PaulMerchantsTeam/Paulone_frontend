package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import callApiGeneric

import com.paulmerchants.gold.model.requestmodels.ReqPendingInterstDue
import com.paulmerchants.gold.model.responsemodels.BaseResponse
import com.paulmerchants.gold.model.responsemodels.RespGetLoanOutStandingItem
import com.paulmerchants.gold.model.responsemodels.RespOutstandingLoan
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoldLoanScreenViewModel @Inject constructor(

    private val apiParams: ApiParams
) : ViewModel() {
    var isCalledGoldLoanScreen: Boolean = true
    private val TAG = this.javaClass.name

    val getRespGetLoanOutStandingLiveData = MutableLiveData<BaseResponse<RespOutstandingLoan>>()
    var respGetLoanOutStanding = ArrayList<RespGetLoanOutStandingItem>()




    init {
        Log.d(TAG, ": init_$TAG")
    }

    override fun onCleared() {
        super.onCleared()
        isCalledGoldLoanScreen = false
    }

    fun getLoanOutstanding(location: Location?, context: Context) {
        val request = ReqPendingInterstDue(
            AppSharedPref.getStringValue(Constants.CUSTOMER_ID)
                .toString(),
            AppUtility.getDeviceDetails(location)
        )
        callApiGeneric<RespOutstandingLoan>(
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
            onClientError = { data ->
                when (data.status_code) {
                    400 -> {
                        data.message.showSnackBar()


                    }

                    401 -> {
                        data.message.showSnackBar()


                    }

                    else -> {
                        data.message.showSnackBar()
                    }
                }
            },
            onTokenExpired = { data ->
                getRespGetLoanOutStandingLiveData.postValue(data)
            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }
        )
    }


}