package com.paulmerchants.gold.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {
    private val TAG = "SplashViewModel"

    init {
        Log.d(TAG, ": init_")
    }

    var counter = 0

    fun setValue() {
        if (counter >= 3) return else counter += 1
    }

}