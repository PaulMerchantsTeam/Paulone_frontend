package com.paulmerchants.gold.viewmodels

import android.os.Build
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
import com.paulmerchants.gold.R
import com.paulmerchants.gold.model.GetPendingInrstDueResp
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.RespClosureReceipt
import com.paulmerchants.gold.model.RespCustomersDetails
import com.paulmerchants.gold.model.RespGetLoanOutStanding
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem
import com.paulmerchants.gold.model.RespLoanDueDate
import com.paulmerchants.gold.model.RespLoanRenewalProcess
import com.paulmerchants.gold.model.RespLoanStatment
import com.paulmerchants.gold.model.RespRenewalEligiblity
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.ReGetLoanClosureReceipNew
import com.paulmerchants.gold.model.newmodel.ReqCreateOrder
import com.paulmerchants.gold.model.newmodel.ReqGetLoanStatement
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespCreateOrder
import com.paulmerchants.gold.model.newmodel.RespUpdatePaymentStatus
import com.paulmerchants.gold.model.newmodel.StatusPayment
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.place.Place
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.CUSTOMER_ID
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.setTColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {
    var isCalled: Boolean = true
    var isCalledGoldLoanScreen: Boolean = true
    val paymentData = MutableLiveData<StatusPayment?>()
    var dueLoanSelected: GetPendingInrstDueRespItem? = null
    var respGetLoanOutStanding = ArrayList<RespGetLoanOutStandingItem>()
    var notZero: List<GetPendingInrstDueRespItem> = arrayListOf()
    private var remoteDataList: List<Place>? = null
    private var listOfLocation: List<com.paulmerchants.gold.place.Place>? = null
    val getPendingInterestDuesLiveData = MutableLiveData<GetPendingInrstDueResp>()
    val tokenExpiredResp = MutableLiveData<RespCommon?>()
    val getRespGetLoanOutStandingLiveData = MutableLiveData<RespGetLoanOutStanding>()
    val getRespLoanDueDateLiveData = MutableLiveData<RespLoanDueDate>()
    val getRespClosureReceiptLiveData = MutableLiveData<RespClosureReceipt>()
    val getRespLoanStatmentLiveData = MutableLiveData<RespLoanStatment>()
    val getRespRenewalEligiblityLiveData = MutableLiveData<RespRenewalEligiblity>()
    val getRespLoanRenewalProcessLiveData = MutableLiveData<RespLoanRenewalProcess>()
    var timer: CountDownTimer? = null
    val countNum = MutableLiveData<Long>()
    val countStr = MutableLiveData<String>()
    var isStartAnim = MutableLiveData<Boolean>()
    var remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private val secureFiles: SecureFiles = SecureFiles()
    val responseCreateOrder = MutableLiveData<RespCreateOrder?>()
//    val placesLive = MutableLiveData<MutableList<Place>?>()

    val respPaymentUpdate = MutableLiveData<RespUpdatePaymentStatus?>()

//    init {
//        val configSettings = remoteConfigSettings {
//            minimumFetchIntervalInSeconds = 60
//        }
//        remoteConfig.setConfigSettingsAsync(configSettings)
//        remoteConfig.setDefaultsAsync(R.xml.places)
//        loadData()
//    }

    /* fun getLogin() = viewModelScope.launch {
         Log.d("TAG", "getLogin: //../........")
         retrofitSetup.callApi(true, object : CallHandler<LoginNewResp> {
             override suspend fun sendRequest(apiParams: ApiParams): LoginNewResp {
                 return apiParams.getLogin(
                     LoginReqNew(
                         AppUtility.getDeviceDetails(),
                         BuildConfig.PASSWORD,
                         BuildConfig.USERNAME
                     )
                 )
             }

             override fun success(response: LoginNewResp) {
                 Log.d("TAG", "success: /////////")
                 Log.d("TAG", "success: ......$response")
             }

             override fun error(message: String) {
                 super.error(message)

                 Log.d("TAG", "error: ......$message")
             }
         })
     }*/

    /**
     * [
     *      {
     *          "AcNo":102210000015198,
     *          "InterestDue":3588.0000,
     *          "ProductName":"SUGHAM LOAN  R",
     *          "DueDate":"2024-06-21T00:00:00",
     *          "Fine":1.0000
     *      }
     * ]
     */


    fun getPendingInterestDues(appSharedPref: AppSharedPref?) = viewModelScope.launch {

        retrofitSetup.callApi(true, object : CallHandler<Response<*>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<*> {
                return apiParams.getPendingInterestDues(
                    "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                    ReqpendingInterstDueNew(
                        appSharedPref?.getStringValue(CUSTOMER_ID).toString(),
                        AppUtility.getDeviceDetails()
                    )
                )
            }

            override fun success(response: Response<*>) {
                Log.d("TAG", "success: ......${response.body()}")
                if (response.isSuccessful) {
                    try {
                        val gson = Gson()
                        val respSuccess: RespCommon? = gson.fromJson(
                            gson.toJsonTree(response.body()).asJsonObject,
                            RespCommon::class.java
                        )
                        // Get the plain text response
                        val plainTextResponse = respSuccess?.data
                        // Do something with the plain text response
                        if (plainTextResponse != null) {
                            Log.d("Response", plainTextResponse)
                            val decryptData = decryptKey(
                                BuildConfig.SECRET_KEY_GEN, plainTextResponse
                            )
                            println("decrypt-----$decryptData")
                            val respPending: GetPendingInrstDueResp? =
                                AppUtility.convertStringToJson(decryptData.toString())
//                val respPending = AppUtility.stringToJsonGetPending(decryptData.toString())
                            respPending?.let { resp ->
                                getPendingInterestDuesLiveData.value = resp
                            }
                            println("Str_To_Json------$respPending")
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 401) {
                    if (appSharedPref != null) {
                        getLogin2(appSharedPref)
                    }
                    Log.d("FAILED_401", "400000111111: ...............${response.body()}")
                    val gson = Gson()
                    val respFail: RespCommon? = gson.fromJson(
                        gson.toJsonTree(response.body()).asJsonObject,
                        RespCommon::class.java
                    )
                    tokenExpiredResp.value = respFail
                }

                AppUtility.hideProgressBar()
            }

            override fun error(message: String) {
                super.error(message)
                Log.d("TAG", "error: ......$message")
            }
        })

    }

    fun createOrder(appSharedPref: AppSharedPref?, reqCreateOrder: ReqCreateOrder) =
        viewModelScope.launch {

            retrofitSetup.callApi(true, object : CallHandler<Response<*>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<*> {
                    return apiParams.createOrder(
                        "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                        reqCreateOrder
                    )
                }

                override fun success(response: Response<*>) {
                    Log.d("TAG", "success: ......${response.body()}")
                    if (response.isSuccessful) {
                        try {
                            val gson = Gson()
                            val respSuccess: RespCreateOrder? = gson.fromJson(
                                gson.toJsonTree(response.body()).asJsonObject,
                                RespCreateOrder::class.java
                            )
                            // Get the plain text response
                            val plainTextResponse = respSuccess?.data
                            // Do something with the plain text response
                            Log.d("TAG", "success: .,..$plainTextResponse.")
                            responseCreateOrder.value = respSuccess
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (response.code() == 401) {
                        Log.d("FAILED_401", "400000111111: ...............${response.body()}")
                        val gson = Gson()
                        val respFail: RespCommon? = gson.fromJson(
                            gson.toJsonTree(response.body()).asJsonObject,
                            RespCommon::class.java
                        )
                        tokenExpiredResp.value = respFail
                        if (appSharedPref != null) {
                            getLogin2(appSharedPref)
                        }
                    }

                    AppUtility.hideProgressBar()
                }

                override fun error(message: String) {
                    super.error(message)
                    Log.d("TAG", "error: ......$message")
                }
            })

        }


    fun updatePaymentStatus(
        appSharedPref: AppSharedPref?,
        status: String?,
        razorpay_payment_id: String,
        razorpay_order_id: String,
        razorpay_signature: String,
        custId: String,
        amount: Double?,
        contactCount: Int,
        companyName: String = "PAUL MERCHANTS",
        currency: String = "INR",
        description: String,
    ) = viewModelScope.launch {
        retrofitSetup.callApi(false, object : CallHandler<Response<*>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<*> {
                return apiParams.updatePaymentStatus(
                    "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                    amount = amount,
                    contactCount = contactCount,
                    companyName = companyName,
                    currency = currency,
                    description = description,
                    status = status.toString(),
                    razorpayPaymentId = razorpay_payment_id,
                    razorpayOrderId = razorpay_order_id,
                    razorpaySignature = razorpay_signature,
                    custId = custId,
                    acNo = dueLoanSelected?.AcNo.toString(),
                    makerId = "12545as",
                    macID = Build.ID,
                    deviceDetailsDTO = AppUtility.getDeviceDetails(),
                    isCustom = false
                )
            }

            override fun success(response: Response<*>) {
                Log.d("TAG", "success: ......${response.body()}")
                if (response.isSuccessful) {
                    try {
                        val gson = Gson()
                        val respSuccess: RespUpdatePaymentStatus? = gson.fromJson(
                            gson.toJsonTree(response.body()).asJsonObject,
                            RespUpdatePaymentStatus::class.java
                        )
                        respSuccess?.response_message.showSnackBar()
                        respPaymentUpdate.value = respSuccess
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 401) {
                    Log.d("FAILED_401", "400000111111: ...............${response.body()}")
                    val gson = Gson()
                    val respFail: RespCommon? = gson.fromJson(
                        gson.toJsonTree(response.body()).asJsonObject,
                        RespCommon::class.java
                    )
                    tokenExpiredResp.value = respFail
                    if (appSharedPref != null) {
                        getLogin2(appSharedPref)
                    }
                }
                AppUtility.hideProgressBar()
            }

            override fun error(message: String) {
                super.error(message)
                Log.d("TAG", "error: ......$message")
            }
        })

    }

    fun getLogin2(appSharedPref: AppSharedPref?) = viewModelScope.launch {
        Log.d("TAG", "getLogin: //../........")
        retrofitSetup.callApi(true, object : CallHandler<Response<LoginNewResp>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<LoginNewResp> {
                return apiParams.getLogin(
                    LoginReqNew(
                        AppUtility.getDeviceDetails(),
                        BuildConfig.PASSWORD,
                        BuildConfig.USERNAME
                    )
                )
            }

            override fun success(response: Response<LoginNewResp>) {
                Log.d("TAG", "success: ......$response")
                response.body()?.statusCode?.let {
                    appSharedPref?.putStringValue(
                        Constants.AUTH_STATUS,
                        it
                    )
                }
                response.body()?.token?.let { appSharedPref?.putStringValue(JWT_TOKEN, it) }
                AppUtility.hideProgressBar()
            }

            override fun error(message: String) {
                super.error(message)
                Log.d("TAG", "error: ......$message")
                AppUtility.hideProgressBar()
            }
        })
    }

    fun getLoanOutstanding(appSharedPref: AppSharedPref?) = viewModelScope.launch {

        retrofitSetup.callApi(true, object : CallHandler<Response<RespCommon>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<RespCommon> {
                return apiParams.getLoanOutstanding(
                    "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                    ReqpendingInterstDueNew(
                        appSharedPref?.getStringValue(CUSTOMER_ID).toString(),
                        AppUtility.getDeviceDetails()
                    )
                )
            }

            override fun success(response: Response<RespCommon>) {
                try {
                    // Get the plain text response
                    val plainTextResponse = response.body()?.data
                    // Do something with the plain text response
                    if (plainTextResponse != null) {
                        Log.d("Response", plainTextResponse)
                        val decryptData = decryptKey(
                            BuildConfig.SECRET_KEY_GEN, plainTextResponse
                        )
                        println("decrypt-----$decryptData")
                        val respPending: RespGetLoanOutStanding? =
                            AppUtility.convertStringToJson(decryptData.toString())
//                val respPending = AppUtility.stringToJsonGetPending(decryptData.toString())
                        respPending?.let { resp ->
                            getRespGetLoanOutStandingLiveData.value = resp
                        }
                        println("Str_To_Json------$respPending")
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

    /*fun getLoanDueDate() = viewModelScope.launch {
        try {
            AppSharedPref.getStringValue(JWT_TOKEN)?.let {
                val response = apiParams.getLoanDueDate(
                    it, AppSharedPref.getStringValue(CUSTOMER_ID).toString()
                )
                // Get the plain text response
                val plainTextResponse = response.string()

                // Do something with the plain text response
                Log.d("Response", plainTextResponse)

                val decryptData = decryptKey(
                    BuildConfig.SECRET_KEY_GEN, plainTextResponse
                )
                println("decrypt-----$decryptData")
                val respPending: RespLoanDueDate? =
                    AppUtility.convertStringToJson(decryptData.toString())
//                val respPending = AppUtility.stringToJsonGetPending(decryptData.toString())
                respPending?.let { resp ->
                    getRespLoanDueDateLiveData.value = resp
                }
                println("Str_To_Json------$respPending")

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        AppUtility.hideProgressBar()
    }*/

    fun getLoanClosureReceipt(appSharedPref: AppSharedPref?, accNum: String) =
        viewModelScope.launch {

            retrofitSetup.callApi(true, object : CallHandler<Response<RespCommon>> {
                override suspend fun sendRequest(apiParams: ApiParams): Response<RespCommon> {
                    return apiParams.getLoanClosureReceipt(
                        "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                        ReGetLoanClosureReceipNew(
                            accNum,
                            AppUtility.getDeviceDetails()
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
//                val respPending = AppUtility.stringToJsonGetPending(decryptData.toString())
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

    /*  fun getRenewalEligibility() = viewModelScope.launch {
          try {
              AppSharedPref.getStringValue(JWT_TOKEN)?.let {
                  val response = apiParams.getRenewalEligibility(
                      it, AppSharedPref.getStringValue(CUSTOMER_ID).toString()
                  )
                  // Get the plain text response
                  val plainTextResponse = response.string()

                  // Do something with the plain text response
                  Log.d("Response", plainTextResponse)

                  val decryptData = decryptKey(
                      BuildConfig.SECRET_KEY_GEN, plainTextResponse
                  )
                  println("decrypt-----$decryptData")
                  val respPending: RespRenewalEligiblity? =
                      AppUtility.convertStringToJson(decryptData.toString())
  //                val respPending = AppUtility.stringToJsonGetPending(decryptData.toString())
                  respPending?.let { resp ->
                      getRespRenewalEligiblityLiveData.value = resp
                  }
                  println("Str_To_Json------$respPending")

              }
          } catch (e: Exception) {
              e.printStackTrace()
          }
          AppUtility.hideProgressBar()
      }

      fun getLoanRenewalProcess() = viewModelScope.launch {
          try {
              AppSharedPref.getStringValue(JWT_TOKEN)?.let {
                  val response = apiParams.getLoanRenewalProcess(
                      it, AppSharedPref.getStringValue(CUSTOMER_ID).toString()
                  )
                  // Get the plain text response
                  val plainTextResponse = response.string()

                  // Do something with the plain text response
                  Log.d("Response", plainTextResponse)

                  val decryptData = decryptKey(
                      BuildConfig.SECRET_KEY_GEN, plainTextResponse
                  )
                  println("decrypt-----$decryptData")
                  val respPending: RespLoanRenewalProcess? =
                      AppUtility.convertStringToJson(decryptData.toString())
  //                val respPending = AppUtility.stringToJsonGetPending(decryptData.toString())
                  respPending?.let { resp ->
                      getRespLoanRenewalProcessLiveData.value = resp
                  }
                  println("Str_To_Json------$respPending")

              }
          } catch (e: Exception) {
              e.printStackTrace()
          }
          AppUtility.hideProgressBar()
      }*/


    fun getLoanStatement(
        appSharedPref: AppSharedPref?,
        accNum: String,
        fromDat: String,
        toDate: String,
    ) = viewModelScope.launch {


        retrofitSetup.callApi(true, object : CallHandler<Response<RespCommon>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<RespCommon> {
                return apiParams.getLoanStatement(
                    "Bearer ${appSharedPref?.getStringValue(JWT_TOKEN).toString()}",
                    ReqGetLoanStatement(accNum, fromDat, toDate, AppUtility.getDeviceDetails())
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
//                val respPending = AppUtility.stringToJsonGetPending(decryptData.toString())
                            respPending?.let { resp ->
                                getRespLoanStatmentLiveData.value = resp
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




//    fun filterLocation(region: String?) {
//        Log.d("TAG", "filterLocation: $region")
//        when (region) {
//            "ch", "chan", "chand", "chandi", "chandig", "chandigarh", "Chandigarh" -> {
//                val ch = listOfLocation?.filter {
//                    it.city == "ch"
//                }
//                placesLive.postValue(ch as MutableList<Place>?)
//            }
//
//            "pb", "punjab", "panjab", "pun", "punj" -> {
//                val pb = listOfLocation?.filter {
//                    it.city == "pb"
//                }
//                placesLive.postValue(pb as MutableList<Place>?)
//            }
//
//            else -> {
//                placesLive.postValue(listOfLocation as MutableList<Place>?)
//            }
//        }
//    }

//    fun loadData() {
//        remoteConfig.fetchAndActivate().addOnCompleteListener { it ->
//            if (it.isSuccessful) {
//                val updated = it.result
//                Log.d("loadData", ": $updated")
//                val data = remoteConfig.getString("places")
//                Log.d("loadData", data)
//                val gson = Gson()
//                remoteDataList = gson.fromJson(data, Array<Place>::class.java).toMutableList()
//                listOfLocation = remoteDataList
//                placesLive.postValue(remoteDataList as MutableList<Place>)
//
////                placesLive.postValue(remoteDataList)
//                Log.d("datalist", remoteDataList.toString())
//            } else {
//                Log.d("loadData", "Else:")
//            }
//        }
//    }

}


/**
 * RESPONSE --  DECRYPT --
 */