package com.paulmerchants.gold

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class MyLifecycleObserver(private val context: Context, private val listener: DialogListener) :
    LifecycleObserver {
    private fun isAutomaticDateTimeEnabled(context: Context): Boolean {
        return Settings.Global.getInt(context.contentResolver, Settings.Global.AUTO_TIME, 0) == 1
    }

    private fun isAutomaticTimeZoneEnabled(context: Context): Boolean {
        return Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.AUTO_TIME_ZONE,
            0
        ) == 1
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {

        // Handle the onResume event


        if (isAutomaticDateTimeEnabled(context) && isAutomaticTimeZoneEnabled(context)) {
            // Auto time is disabled - show the dialog
            listener.dismissAutoTimeDisabledDialog()

        } else {
            listener.showAutoTimeDisabledDialog()
        }

    }

    interface DialogListener {
        fun showAutoTimeDisabledDialog()
        fun dismissAutoTimeDisabledDialog()
    }


}

