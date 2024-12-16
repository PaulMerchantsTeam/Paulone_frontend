package com.paulmerchants.gold.viewmodels

import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.RespClosureReceipt
import com.paulmerchants.gold.model.RespLoanStatment
import com.paulmerchants.gold.model.newmodel.GeOtStandingRespObj
import com.paulmerchants.gold.model.newmodel.GepPendingRespObj
import com.paulmerchants.gold.model.newmodel.GetPendingResponse
import com.paulmerchants.gold.model.newmodel.ReGetLoanClosureReceipNew
import com.paulmerchants.gold.model.newmodel.ReqGetLoanStatement
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespCreateOrder
import com.paulmerchants.gold.model.newmodel.RespGetLOanOutStanding
import com.paulmerchants.gold.model.newmodel.RespUnderMain
import com.paulmerchants.gold.model.newmodel.RespUpdatePaymentStatus
import com.paulmerchants.gold.model.newmodel.StatusPayment
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants.CUSTOMER_ID
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {

    val paymentData = MutableLiveData<StatusPayment?>()
    var dueLoanSelected: GetPendingInrstDueRespItem? = null

    var notZero: List<GetPendingInrstDueRespItem>? = arrayListOf()

    val getPendingInterestDuesLiveData = MutableLiveData<GepPendingRespObj?>()
    val getPendingInterestDuesLiveData1 = MutableLiveData<GepPendingRespObj?>()
    val tokenExpiredResp = MutableLiveData<RespCommon?>()
    val getRespGetLoanOutStandingLiveData = MutableLiveData<GeOtStandingRespObj>()

    val getRespClosureReceiptLiveData = MutableLiveData<RespClosureReceipt>()
    val getRespLoanStatementLiveData = MutableLiveData<RespLoanStatment>()

    var timer: CountDownTimer? = null
    val countNum = MutableLiveData<Long>()

    var isStartAnim = MutableLiveData<Boolean>()
    private var remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    val responseCreateOrder = MutableLiveData<RespCreateOrder?>()
    val isUnderMainLiveData = MutableLiveData<RespUnderMain>()
    val isRemoteConfigCheck = MutableLiveData<Boolean>()


    val respPaymentUpdate = MutableLiveData<RespUpdatePaymentStatus?>()

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 30
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        checkForDownFromRemoteConfig()
    }


    fun getUnderMaintenanceStatus() = viewModelScope.launch {
        try {

            val response = apiParams.isUnderMaintenance()
            // Get the plain text response
            val plainTextResponse = response.string()

            // Do something with the plain text response
            Log.d("Response", plainTextResponse)

            val decryptData = decryptKey(
                BuildConfig.SECRET_KEY_UAT,
                plainTextResponse
            )
            println("decrypt-----$decryptData")
            val gson = Gson()
//           val  typeToken = object : TypeToken<BaseResponse<RespUnderMain>>() {}
//            val respPending = gson.fromJson<BaseResponse<DataDown>>(decryptData.toString(),typeToken.type)
            val respPending = gson.fromJson(decryptData.toString(), RespUnderMain::class.java)

            isUnderMainLiveData.value = respPending
            println("Str_To_Json------$respPending")


        } catch (e: Exception) {
            e.printStackTrace()
        }
        AppUtility.hideProgressBar()
    }

    fun getPendingInterestDues(location: Location?) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqpendingInterstDueNew(
                    AppSharedPref.getStringValue(CUSTOMER_ID)
                        .toString(),
                    AppUtility.getDeviceDetails(location)
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), encryptedString.toString())

                val response = apiParams.getPendingInterestDues(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }",
                    requestBody
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
                    gson.fromJson(decryptData.toString(), GetPendingResponse::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {


                        getPendingInterestDuesLiveData.value =
                            respPending.data

                    } else {
                        "${it.message}".showSnackBar()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }


    fun getLoanOutstanding(location: Location?) =
        viewModelScope.launch {
            try {
                val gson = Gson()
                val request = ReqpendingInterstDueNew(
                    AppSharedPref.getStringValue(CUSTOMER_ID)
                        .toString(),
                    AppUtility.getDeviceDetails(location)
                )
                val jsonString = gson.toJson(request)
                val encryptedString = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString.toString())
                val requestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), encryptedString.toString())

                val response = apiParams.getLoanOutstanding(
                    "Bearer ${
                        AppSharedPref.getStringValue(JWT_TOKEN).toString()
                    }",
                    requestBody
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
                    gson.fromJson(decryptData.toString(), RespGetLOanOutStanding::class.java)
                println("Str_To_Json------$respPending")
                respPending?.let {
                    if (it.status_code == 200) {


                        getRespGetLoanOutStandingLiveData.value =
                            respPending?.data

                    } else {
                        "${it.message}".showSnackBar()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            AppUtility.hideProgressBar()

        }


    fun getLoanClosureReceipt(accNum: String, location: Location?) =
        viewModelScope.launch {

            retrofitSetup.callApi(false, object : CallHandler<Response<RespCommon>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespCommon> {
                    return apiParams.getLoanClosureReceipt(
                        "Bearer ${
                            AppSharedPref.getStringValue(JWT_TOKEN).toString()
                        }",
                        ReGetLoanClosureReceipNew(
                            accNum,
                            AppUtility.getDeviceDetails(location)
                        )
                    )
                }

                override fun success(response: Response<RespCommon>) {
                    try {
                        if (response.isSuccessful) {
                            val plainTextResponse = response.body()?.data

                            // Get the plain text response

                            // Do something with the plain text response
                            if (plainTextResponse != null) {
                                Log.d("Response", plainTextResponse)
                                val decryptData = decryptKey(
                                    BuildConfig.SECRET_KEY_GEN, plainTextResponse
                                )
                                println("decrypt-----$decryptData")
                                val respPending: RespClosureReceipt? =
                                    AppUtility.convertStringToJson(decryptData.toString())

                                respPending?.let { resp ->
                                    getRespClosureReceiptLiveData.value = resp
                                }
                                println("Str_To_Json------$respPending")
                            }
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

    fun getLoanStatement(
        accNum: String,
        fromDat: String,
        toDate: String,
        location: Location?,
    ) = viewModelScope.launch {


        retrofitSetup.callApi(false, object : CallHandler<Response<RespCommon>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<RespCommon> {
                return apiParams.getLoanStatement(
                    "Bearer ${AppSharedPref.getStringValue(JWT_TOKEN).toString()}",
                    ReqGetLoanStatement(
                        accNum,
                        fromDat,
                        toDate,
                        AppUtility.getDeviceDetails(location)
                    )
                )
            }

            override fun success(response: Response<RespCommon>) {
                try {
                    if (response.isSuccessful) {
                        val plainTextResponse = response.body()?.data
                        // Do something with the plain text response
                        if (plainTextResponse != null) {
                            // Get the plain text response
                            // Do something with the plain text response
                            Log.d("Response", plainTextResponse)
                            val decryptData = decryptKey(
                                BuildConfig.SECRET_KEY_GEN, plainTextResponse
                            )
                            println("decrypt-----$decryptData")
                            val respPending: RespLoanStatment? =
                                AppUtility.convertStringToJson(decryptData.toString())

                            respPending?.let { resp ->
                                getRespLoanStatementLiveData.value = resp
                            }
                            println("Str_To_Json------$respPending")
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
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


}

