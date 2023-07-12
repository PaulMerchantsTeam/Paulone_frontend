package com.paulmerchants.gold.viewmodels

import android.content.Context
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
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespClosureReceipt
import com.paulmerchants.gold.model.RespCustomersDetails
import com.paulmerchants.gold.model.RespGetLoanOutStanding
import com.paulmerchants.gold.model.RespLoanDueDate
import com.paulmerchants.gold.model.RespLoanRenewalProcess
import com.paulmerchants.gold.model.RespRenewalEligiblity
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.place.Place
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants.CUSTOMER_ID
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.utility.Constants.ACC_NUM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) : ViewModel() {

    var notZero: List<GetPendingInrstDueRespItem> = arrayListOf()
    private var remoteDataList: List<Place>? = null
    private var listOfLocation: List<com.paulmerchants.gold.place.Place>? = null
    val getPendingInterestDuesLiveData = MutableLiveData<GetPendingInrstDueResp>()
    val getRespGetLoanOutStandingLiveData = MutableLiveData<RespGetLoanOutStanding>()
    val getRespLoanDueDateLiveData = MutableLiveData<RespLoanDueDate>()
    val getRespClosureReceiptLiveData = MutableLiveData<RespClosureReceipt>()
    val getRespCustomersDetailsLiveData = MutableLiveData<RespCustomersDetails>()
    val getRespRenewalEligiblityLiveData = MutableLiveData<RespRenewalEligiblity>()
    val getRespLoanRenewalProcessLiveData = MutableLiveData<RespLoanRenewalProcess>()
    var timer: CountDownTimer? = null
    val countNum = MutableLiveData<Long>()
    var isStartAnim = MutableLiveData<Boolean>()
    var remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private lateinit var secureFiles: SecureFiles

    val placesLive = MutableLiveData<MutableList<Place>?>()

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.places)
        loadData()
        secureFiles = SecureFiles()
    }

    fun getLogin() = viewModelScope.launch {
        Log.d("TAG", "getLogin: //../........")
        retrofitSetup.callApi(true, object : CallHandler<ResponseBody> {
            override suspend fun sendRequest(apiParams: ApiParams): ResponseBody {
                return apiParams.getLogin(
                    RequestLogin(
                        BuildConfig.PASSWORD, BuildConfig.USERNAME
                    )
                )
            }

            override fun success(response: ResponseBody) {
                Log.d("TAG", "success: /////////")
                Log.d("TAG", "success: ......$response")
            }

            override fun error(message: String) {
                super.error(message)

                Log.d("TAG", "error: ......$message")
            }
        })
    }

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


    fun getPendingInterestDues(context: Context, date: String) = viewModelScope.launch {
        try {
            AppSharedPref.getStringValue(JWT_TOKEN)?.let {
                val response = apiParams.getPendingInterestDues(
                    it, secureFiles.encryptKey(
                        AppSharedPref.getStringValue(CUSTOMER_ID).toString(),
                        BuildConfig.SECRET_KEY_GEN
                    ).toString(), date
                )
                // Get the plain text response
                val plainTextResponse = response.string()

                // Do something with the plain text response
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
        AppUtility.hideProgressBar()
    }

    fun getLoanOutstanding() = viewModelScope.launch {
        try {
            AppSharedPref.getStringValue(JWT_TOKEN)?.let {
                val response = apiParams.getLoanOutstanding(
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

    fun getLoanDueDate() = viewModelScope.launch {
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
    }

    fun getLoanClosureReceipt() = viewModelScope.launch {
        try {
            AppSharedPref.getStringValue(JWT_TOKEN)?.let {
                val response = apiParams.getLoanClosureReceipt(
                    it, AppSharedPref.getStringValue(ACC_NUM).toString()
                )
                // Get the plain text response
                val plainTextResponse = response.string()

                // Do something with the plain text response
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
        AppUtility.hideProgressBar()
    }

    fun getRenewalEligibility() = viewModelScope.launch {
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
    }

    fun getCustomerDetails() = viewModelScope.launch {
        try {
            AppSharedPref.getStringValue(JWT_TOKEN)?.let {
                val response = apiParams.getCustomerDetails(
                    it, secureFiles.encryptKey(
                        AppSharedPref.getStringValue(CUSTOMER_ID).toString(),
                        BuildConfig.SECRET_KEY_GEN
                    ).toString()
                )
                // Get the plain text response
                val plainTextResponse = response.string()

                // Do something with the plain text response
                Log.d("Response", plainTextResponse)

                val decryptData = decryptKey(
                    BuildConfig.SECRET_KEY_GEN, plainTextResponse
                )
                println("decrypt-----$decryptData")
                val respPending: RespCustomersDetails? =
                    AppUtility.convertStringToJson(decryptData.toString())
//                val respPending = AppUtility.stringToJsonGetPending(decryptData.toString())
                respPending?.let { resp ->
                    getRespCustomersDetailsLiveData.value = resp
                }
                println("Str_To_Json------$respPending")

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        AppUtility.hideProgressBar()
    }


    fun timerStart(millis: Long) {
        timer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countNum.postValue(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                countNum.postValue(0)
            }
        }
        timer?.start()
    }

    fun filterLocation(region: String?) {
        Log.d("TAG", "filterLocation: $region")
        when (region) {
            "ch", "chan", "chand", "chandi", "chandig", "chandigarh", "Chandigarh" -> {
                val ch = listOfLocation?.filter {
                    it.city == "ch"
                }
                placesLive.postValue(ch as MutableList<Place>?)
            }

            "pb", "punjab", "panjab", "pun", "punj" -> {
                val pb = listOfLocation?.filter {
                    it.city == "pb"
                }
                placesLive.postValue(pb as MutableList<Place>?)
            }

            else -> {
                placesLive.postValue(listOfLocation as MutableList<Place>?)
            }
        }
    }

    fun loadData() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { it ->
            if (it.isSuccessful) {
                val updated = it.result
                Log.d("loadData", ": $updated")
                val data = remoteConfig.getString("places")
                Log.d("loadData", data)
                val gson = Gson()
                remoteDataList = gson.fromJson(data, Array<Place>::class.java).toMutableList()
                listOfLocation = remoteDataList
                placesLive.postValue(remoteDataList as MutableList<Place>)

//                placesLive.postValue(remoteDataList)
                Log.d("datalist", remoteDataList.toString())
            } else {
                Log.d("loadData", "Else:")
            }
        }
    }

}


/**
 * RESPONSE --  DECRYPT --
 */