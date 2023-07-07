package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants.AUTH_STATUS
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
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


    fun getLogin2(context: Context) = viewModelScope.launch {
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

        val decryptData = decryptKey(
            BuildConfig.SECRET_KEY_GEN,
            plainTextResponse
        )
        println("decrypt-----$decryptData")
        val respLogin = AppUtility.stringToJson(decryptData.toString())
        println("Str_To_Json------$respLogin")
        AppSharedPref.putStringValue(AUTH_STATUS, respLogin.Status.toString())
        if (respLogin.Status == true) {
            AppSharedPref.putStringValue(JWT_TOKEN, respLogin.JWToken.toString())
        } else {
            Toast.makeText(context, "Some thing went wrong.", Toast.LENGTH_SHORT).show()
        }
        AppUtility.hideProgressBar()
    }

    var counter = 0

    fun setValue() {
        if (counter >= 3) return else counter += 1
    }

}