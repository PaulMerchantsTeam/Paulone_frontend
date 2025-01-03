package com.paulmerchants.gold.viewmodels

import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import callApiGeneric
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.paulmerchants.gold.model.requestmodels.ReqCreateOrder
import com.paulmerchants.gold.model.requestmodels.ReqPendingInterstDue
import com.paulmerchants.gold.model.requestmodels.RequestMTransaction
import com.paulmerchants.gold.model.responsemodels.BaseResponse
import com.paulmerchants.gold.model.responsemodels.RespCreateOrder
import com.paulmerchants.gold.model.responsemodels.RespDataDown
import com.paulmerchants.gold.model.responsemodels.RespGetCustomer
import com.paulmerchants.gold.model.responsemodels.RespMTransaction
import com.paulmerchants.gold.model.responsemodels.RespPaymentMethod
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.AppUtility.showSnackBarForPayment
import com.paulmerchants.gold.utility.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(

    private val apiParams: ApiParams,
) : ViewModel() {
    private val TAG = this.javaClass.name
    var isCalled = true
    private var remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    val responseCreateOrder = MutableLiveData<BaseResponse< RespCreateOrder>?>()
    val respPaymentUpdate = MutableLiveData<BaseResponse<RespMTransaction>?>()
    val getPaymentMethod = MutableLiveData<BaseResponse<List<RespPaymentMethod>>?>()
    val getRespCustomersDetailsLiveData = MutableLiveData<BaseResponse<RespGetCustomer>>()
    val isUnderMainLiveData = MutableLiveData<BaseResponse<RespDataDown>>()
    val isRemoteConfigCheck = MutableLiveData<Boolean>()

    init {
        Log.d(TAG, ": init_$TAG")
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 30
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    fun checkForDownFromRemoteConfig() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { it ->
            if (it.isSuccessful) {
                val updated = it.result
                Log.d("loadData", ": $updated")
                val data = remoteConfig.getBoolean("isAppDown")
                Log.d("loadData", "$data")
                isRemoteConfigCheck.value = data
            } else {
                Log.d("loadData", "Else:")
            }
        }
    }

    fun getUnderMaintenanceStatusCheck(context: Context) {
        callApiGeneric<RespDataDown>(
            request = "",
            progress = true,
            context = context,
            apiCall = { requestBody ->
                apiParams.isUnderMaintenance()
            },
            onSuccess = { data ->
                isUnderMainLiveData.postValue(data)
            },
            onClientError = { data ->
                when (data.status_code) {
                    400 -> {
                        data.message.showSnackBarForPayment()


                    }

                    401 -> {
                        data.message.showSnackBarForPayment()


                    }

                    else -> {
                        data.message.showSnackBarForPayment()
                    }
                }
            },
            onTokenExpired = { data ->
                Log.d("TAG", "verifyOtp: Invalid Token: ${data.message}")


            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBarForPayment()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }

        )
    }

    fun getUnderMaintenanceStatus(reqCreateOrder: ReqCreateOrder, context: Context) {
        callApiGeneric<RespDataDown>(
            request = "",
            progress = true,
            context = context,
            apiCall = { requestBody ->
                apiParams.isUnderMaintenance()
            },
            onSuccess = { data ->
                if (data.data?.down == false) {
                    createOrder(reqCreateOrder, context)
                } else {
                    isUnderMainLiveData.postValue(data)
                }

            },
            onClientError = { data ->
                when (data.status_code) {
                    400 -> {
                        data.message.showSnackBarForPayment()


                    }

                    401 -> {
                        data.message.showSnackBarForPayment()


                    }

                    else -> {
                        data.message.showSnackBarForPayment()
                    }
                }
            },
            onTokenExpired = { data ->


            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBar()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }
        )
    }

    fun getCustomerDetails(location: Location?, context: Context) {
        val request = ReqPendingInterstDue(
            AppSharedPref.getStringValue(Constants.CUSTOMER_ID).toString(),
            AppUtility.getDeviceDetails(location)
        )
        callApiGeneric<RespGetCustomer>(
            request = request,
            progress = false,
            context = context,
            apiCall = { requestBody ->
                apiParams.getCustomerDetails(
                    "Bearer ${
                        AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                    }", requestBody
                )
            },
            onSuccess = { data ->
                AppSharedPref.putStringValue(
                    Constants.CUSTOMER_FULL_DATA,
                    data.toString()
                )
                AppSharedPref.putStringValue(
                    Constants.CUST_EMAIL,
                    data.data?.email.toString()
                )
                getRespCustomersDetailsLiveData.postValue(
                    data
                )


            },
            onClientError = { data ->
                when (data.status_code) {
                    400 -> {
                        data.message.showSnackBarForPayment()


                    }

                    401 -> {
                        data.message.showSnackBarForPayment()


                    }

                    else -> {
                        data.message.showSnackBarForPayment()
                    }
                }
            },
            onTokenExpired = { data ->


                getRespCustomersDetailsLiveData.postValue(
                    data
                )


            },
            onUnexpectedError = { errorMessage ->
                errorMessage.showSnackBarForPayment()
                Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

            }
        )
    }

    fun getPaymentMethod(context: Context) =
        viewModelScope.launch {


            callApiGeneric<List<RespPaymentMethod>>(
                request = "",
                progress = false,
                context = context,
                apiCall = { requestBody ->
                    apiParams.getPaymentMethod(
                        "Bearer ${
                            AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                        }"
                    )
                },
                onSuccess = { data ->
                    getPaymentMethod.postValue(data)


                },
                onClientError = { data ->
                    when (data.status_code) {
                        400 -> {
                            data.message.showSnackBarForPayment()


                        }

                        401 -> {
                            data.message.showSnackBarForPayment()


                        }

                        else -> {
                            data.message.showSnackBarForPayment()

                        }
                    }
                },
                onTokenExpired = { data ->
                    getPaymentMethod.postValue(data)
                },
                onUnexpectedError = { errorMessage ->
                    errorMessage.showSnackBarForPayment()
                    Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

                }
            )


        }


    private fun createOrder(reqCreateOrder: ReqCreateOrder, context: Context) =
        viewModelScope.launch {

            callApiGeneric<RespCreateOrder>(
                request = reqCreateOrder,
                progress = true,
                context = context,
                apiCall = { requestBody ->
                    apiParams.createOrder(
                        "Bearer ${
                            AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                        }", requestBody
                    )
                },
                onSuccess = { data ->
                    responseCreateOrder.postValue(data)


                },
                onClientError = { data ->
                    when (data.status_code) {
                        400 -> {
                            data.message.showSnackBarForPayment()


                        }

                        401 -> {
                            data.message.showSnackBarForPayment()


                        }

                        else -> {
                            data.message.showSnackBarForPayment()

                        }
                    }
                },
                onTokenExpired = { data ->
                    responseCreateOrder.postValue(data)


                },
                onUnexpectedError = { errorMessage ->
//                    errorMessage.showSnackBarForPayment()
                    Log.d("TAG", "verifyOtp: Invalid Token: $errorMessage")

                }
            )


        }


    fun mTransaction(
        activity: Activity,
        status: String?,
        razorpay_payment_id: String,
        razorpay_order_id: String,
        razorpay_signature: String,
        custId: String,
        amount: Double?,
        currency: String = "INR",
        description: String = "desc_payment",
        account: String,
        location: Location?,
    ) =

        viewModelScope.launch {


            val request = RequestMTransaction(
                ac_no = account,
                amount = amount,
                currency = currency,
                cust_id = custId,
                description = description,
                mac_id = Build.ID,
                maker_id = "12545as",
                razorpay_order_id = razorpay_order_id,
                razorpay_payment_id = razorpay_payment_id,
                razorpay_signature = razorpay_signature,
                status = status,
                device_details_dto = AppUtility.getDeviceDetails(location)
            )

            callApiGeneric<RespMTransaction>(
                request = request,
                progress = true,
                context = activity,
                apiCall = { requestBody ->
                    apiParams.mTransaction(
                        "Bearer ${
                            AppSharedPref.getStringValue(Constants.JWT_TOKEN).toString()
                        }", requestBody
                    )
                },
                onSuccess = { data ->
                    respPaymentUpdate.postValue(data)
                    AppSharedPref.putStringValue(
                        Constants.PAYMENT_ID,
                        data.data?.payment_id.toString()
                    )


                },
                onClientError = { data ->
                    when (data.status_code) {
                        400 -> {
                            respPaymentUpdate.postValue(data)


                        }

                        401 -> {
                            respPaymentUpdate.postValue(data)


                        }


                        else -> {
                            respPaymentUpdate.postValue(data)

                        }
                    }
                },
                onTokenExpired = { data ->
                    respPaymentUpdate.postValue(data)

                },
                onUnexpectedError = { errorMessage ->

                    errorMessage.showSnackBarForPayment()

                }

            )


        }


}