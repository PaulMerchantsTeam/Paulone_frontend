package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.usedModels.BaseResponse
import com.paulmerchants.gold.model.newmodel.PayReceipt
import com.paulmerchants.gold.model.newmodel.RespPayReceipt
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.networks.callApiGeneric
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
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {

    private val TAG = this.javaClass.name
    val paidReceipt = MutableLiveData<BaseResponse<PayReceipt>>()
    init {
        Log.d(TAG, ": init_$TAG")
    }

    fun getPaidReceipt(orderId: String = "", paymentId: String = "",context: Context) =
        viewModelScope.launch {
            val encryptedOrderId = encryptKey(BuildConfig.SECRET_KEY_UAT, orderId)
            val encryptedPaymentId = encryptKey(BuildConfig.SECRET_KEY_UAT, paymentId)


                callApiGeneric<PayReceipt>(
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

    fun getPaidReceipt1(orderId: String = "", paymentId: String = "") =
        viewModelScope.launch {
            try {
                val gson = Gson()
                Log.d(TAG, "getPaidReceipt orderId: $orderId")
                Log.d(TAG, "getPaidReceipt paymentId: $paymentId")
                val encryptedOrderId = encryptKey(BuildConfig.SECRET_KEY_UAT, orderId)
                val encryptedPaymentId = encryptKey(BuildConfig.SECRET_KEY_UAT, paymentId)

                val response = apiParams.getPaidReceipt(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }",
                    order_id = (if (orderId.isEmpty()) "" else encryptedOrderId),
                    payment_id = (if (paymentId.isEmpty()) "" else encryptedPaymentId)

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
                    gson.fromJson(decryptData.toString(), RespPayReceipt::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {


//                        paidReceipt.value = it

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