package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
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
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.place.Place
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.CUSTOMER_ID
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import com.shacklabs.quicke.remote.networks.RetrofitSetup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.bouncycastle.asn1.ocsp.ResponseBytes
import retrofit2.Converter
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val retrofitSetup: RetrofitSetup,
    private val apiParams: ApiParams,
) :
    ViewModel() {
    private var remoteDataList: List<Place>? = null
    private var listOfLocation: List<com.paulmerchants.gold.place.Place>? = null
    val getPendingInterestDuesLiveData = MutableLiveData<GetPendingInrstDueResp>()
    var timer: CountDownTimer? = null
    val countNum = MutableLiveData<Long>()
    var isStartAnim = MutableLiveData<Boolean>()
    var remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig


    val placesLive = MutableLiveData<MutableList<Place>?>()

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.places)
        loadData()
    }

    /**
     * [
     * {
     * "AcNo":102210000015198,
     * "InterestDue":3588.0000,
     * "ProductName":"SUGHAM LOAN  R",
     * "DueDate":"2024-06-21T00:00:00",
     * "Fine":1.0000
     * }
     * ]
     */


    fun getPendingInterestDues(context: Context, date: String) = viewModelScope.launch {
        try {
            AppSharedPref.getStringValue(JWT_TOKEN)?.let {
                val response = apiParams.getPendingInterestDues(
                    it, AppSharedPref.getStringValue(CUSTOMER_ID).toString(), date
                )
                // Get the plain text response
                val plainTextResponse = response.string()

                // Do something with the plain text response
                Log.d("Response", plainTextResponse)

                val decryptData = decryptKey(
                    BuildConfig.SECRET_KEY_GEN,
                    plainTextResponse
                )
                println("decrypt-----$decryptData")
                val respPending = AppUtility.stringToJsonGetPending(decryptData.toString())
                getPendingInterestDuesLiveData.value = respPending
                println("Str_To_Json------$respPending")

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        AppUtility.hideProgressBar()
    }


    fun getLogin() = viewModelScope.launch {
        Log.d("TAG", "getLogin: //../........")
        retrofitSetup.callApi(true, object : CallHandler<ResponseBody> {
            override suspend fun sendRequest(apiParams: ApiParams): ResponseBody {
                return apiParams.getLogin(
                    RequestLogin(
                        BuildConfig.PASSWORD,
                        BuildConfig.USERNAME
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
                remoteDataList =
                    gson.fromJson(data, Array<Place>::class.java).toMutableList()
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