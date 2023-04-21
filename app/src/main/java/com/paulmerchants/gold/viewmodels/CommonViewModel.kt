package com.paulmerchants.gold.viewmodels

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.paulmerchants.gold.R
import com.paulmerchants.gold.place.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor() : ViewModel() {
    private var remoteDataList: List<Place>? = null
    private var listOfLocation: List<com.paulmerchants.gold.place.Place>? = null

    var timer: CountDownTimer? = null
    val countNum = MutableLiveData<Long>()

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