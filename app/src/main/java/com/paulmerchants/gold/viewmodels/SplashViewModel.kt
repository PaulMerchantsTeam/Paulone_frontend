package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants.AUTH_STATUS
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val apiParams: ApiParams,
    private val retrofitSetup: RetrofitSetup,
) : ViewModel() {
    private val TAG = "SplashViewModel"

    //"pml", "FU510N@pro"
    init {
        Log.d(TAG, ": init_")
    }


    var counter = 0

    fun setValue() {
        if (counter >= 3) return else counter += 1
    }

}