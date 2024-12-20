package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.responsemodels.BaseResponse
import com.paulmerchants.gold.model.responsemodels.RespPaymentReceipt
import com.paulmerchants.gold.model.other.RespPayReceipt

import  callApiGeneric
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TxnReceiptViewModel @Inject constructor(

    private val apiParams: ApiParams,
) : ViewModel() {

    private val TAG = this.javaClass.name
    val paidReceipt = MutableLiveData<BaseResponse<RespPaymentReceipt>>()
    init {
        Log.d(TAG, ": init_$TAG")
    }

    fun getPaidReceipt(orderId: String = "", paymentId: String = "",context: Context) =
        viewModelScope.launch {
            val encryptedOrderId = encryptKey(BuildConfig.SECRET_KEY_UAT, orderId)
            val encryptedPaymentId = encryptKey(BuildConfig.SECRET_KEY_UAT, paymentId)


                callApiGeneric<RespPaymentReceipt>(
                    request = "",
                    progress = true,
                    context = context,
                    apiCall = { requestBody ->
                        apiParams.getPaidReceipt(
                            "Bearer ${
                                AppSharedPref.getStringValue(JWT_TOKEN).toString()
                            }",
                            order_id = (if (orderId.isEmpty()) "" else encryptedOrderId),
                            payment_id = (if (paymentId.isEmpty()) "" else encryptedPaymentId)

                        )
                    },
                    onSuccess = { data ->
                        paidReceipt.postValue(data)



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
                        paidReceipt.postValue(data)

                    },
                    onUnexpectedError = { errorMessage ->
                        errorMessage.showSnackBar()
                        Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

                    }
                )


        }




}