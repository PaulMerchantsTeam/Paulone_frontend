package com.paulmerchants.gold.common

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GoldApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}