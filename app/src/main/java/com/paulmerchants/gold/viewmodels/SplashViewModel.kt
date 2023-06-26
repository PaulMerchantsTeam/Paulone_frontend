package com.paulmerchants.gold.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.remote.ApiParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val apiParams: ApiParams) : ViewModel() {
    private val TAG = "SplashViewModel"

    init {
        Log.d(TAG, ": init_")
    }

    fun getLogin() = viewModelScope.launch {
        apiParams.getLogin(RequestLogin("pml", "FU510N@pro"))
    }


    var counter = 0

    fun setValue() {
        if (counter >= 3) return else counter += 1
    }

}