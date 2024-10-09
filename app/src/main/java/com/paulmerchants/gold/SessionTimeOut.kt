package com.paulmerchants.gold

import android.content.Context
import android.os.Handler
import android.os.Looper

class SessionTimeOut(private val context: Context) {

    private val timeoutDuration: Long = 10 * 60 * 1000 // 10 minutes in milliseconds
    private var handler: Handler? = null
    private var timeoutRunnable: Runnable? = null

    // Start the session timeout when app goes to background
    fun startSessionTimeout(onSessionTimeout: () -> Unit) {
        handler = Handler(Looper.getMainLooper())
        timeoutRunnable = Runnable {
            // This will execute after 10 minutes of inactivity
            onSessionTimeout()
        }
        handler?.postDelayed(timeoutRunnable!!, timeoutDuration)
    }

    // Stop the session timeout when app comes to foreground
    fun stopSessionTimeout() {
        handler?.removeCallbacks(timeoutRunnable!!)
    }
}