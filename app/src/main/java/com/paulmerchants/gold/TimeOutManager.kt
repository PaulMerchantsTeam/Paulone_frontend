package com.paulmerchants.gold



import android.os.Handler
import android.os.Looper

class TimeoutManager(private val timeoutDuration: Long, private val onTimeout: () -> Unit) {

    private val handler = Handler(Looper.getMainLooper())
    private val timeoutRunnable = Runnable { onTimeout() }

    fun start() {
        resetTimeout()
    }

    fun resetTimeout() {
        handler.removeCallbacks(timeoutRunnable)
        handler.postDelayed(timeoutRunnable, timeoutDuration)
    }

    fun stop() {
        handler.removeCallbacks(timeoutRunnable)
    }
}