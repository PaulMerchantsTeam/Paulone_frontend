package com.paulmerchants.gold.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import dagger.hilt.android.lifecycle.HiltViewModel
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