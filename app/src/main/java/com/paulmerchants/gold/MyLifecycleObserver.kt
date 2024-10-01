package com.paulmerchants.gold

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class MyLifecycleObserver(private val context: Context,private val listener:DialogListener) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {

        // Handle the onResume event

        val isAutoTimeEnabled =
            Settings.Global.getInt(context.contentResolver, Settings.Global.AUTO_TIME, 0) == 1
        val isAutoTimeZoneEnabled =
            Settings.Global.getInt(context.contentResolver, Settings.Global.AUTO_TIME_ZONE, 0) == 1

        if (!isAutoTimeEnabled && !isAutoTimeZoneEnabled) {
            // Auto time is disabled - show the dialog
            listener.showAutoTimeDisabledDialog()



        }

    }
    interface DialogListener {
        fun showAutoTimeDisabledDialog()
    }


}

