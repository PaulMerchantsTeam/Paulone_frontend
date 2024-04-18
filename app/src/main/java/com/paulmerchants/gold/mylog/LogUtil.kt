package com.paulmerchants.gold.mylog

import android.util.Log
import androidx.fragment.app.Fragment
import com.paulmerchants.gold.BuildConfig


object LogUtil {

    inline fun runIf(condition: Boolean, block: () -> Unit) {
        if (condition) block()
    }

    fun Fragment.showLogD(message: String) = runIf(BuildConfig.DEBUG) {
        Log.d(javaClass.name, message)
    }

    fun Fragment.showLogE(message: String) = runIf(BuildConfig.DEBUG) {
        Log.e(javaClass.name, message)
    }

    fun Fragment.showLogI(message: String) = runIf(BuildConfig.DEBUG) {
        Log.i(javaClass.name, message)
    }


}