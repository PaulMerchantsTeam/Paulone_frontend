package com.paulmerchants.gold.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.decryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val apiParams: ApiParams) : ViewModel() {
    private val TAG = "SplashViewModel"

    //"pml", "FU510N@pro"
    init {
        Log.d(TAG, ": init_")
    }

    fun getCustomers() = viewModelScope.launch {

    }


    fun getLogin2() = viewModelScope.launch {
        val response = apiParams.getLogin(
            RequestLogin(
                BuildConfig.PASSWORD,
                BuildConfig.USERNAME
            )
        )
        // Get the plain text response
        val plainTextResponse = response.string()

        // Do something with the plain text response
        Log.d("Response", plainTextResponse)

        val a = decryptKey(
            BuildConfig.SECRET_KEY_GEN,
            plainTextResponse
        )
        println("decrypt-----$a")
        val j = AppUtility.stringToJson(a.toString())
        println("Str_To_Json------$j")
    }

    var counter = 0

    fun setValue() {
        if (counter >= 3) return else counter += 1
    }

}