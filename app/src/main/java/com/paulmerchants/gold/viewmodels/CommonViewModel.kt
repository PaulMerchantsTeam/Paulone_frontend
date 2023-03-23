package com.paulmerchants.gold.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor() : ViewModel() {
    var timer: CountDownTimer? = null
    val countNum = MutableLiveData<Long>()

    init {

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

    override fun onCleared() {
        super.onCleared()

    }

}