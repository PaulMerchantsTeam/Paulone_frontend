package com.paulmerchants.gold.common

import android.app.Application
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GoldApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            AppSharedPref().start(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}