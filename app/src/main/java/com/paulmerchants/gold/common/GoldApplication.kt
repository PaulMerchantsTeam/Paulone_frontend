package com.paulmerchants.gold.common

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.aheaditec.talsec_security.security.api.ThreatListener
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess


@HiltAndroidApp
class GoldApplication : Application(), ThreatListener.ThreatDetected {
    companion object {

        private const val PACKAGE_NAME = "com.paulmerchants.gold"
        private val expectedSigningCertificateHashBase64 =
            arrayOf("tQiceFwrHAeXl9mUu6P3vOqGiJyJA2gleIL6Hrd+Ia8=")
        private const val MAIL_ADDRESS = "pmldevspace@gmail.com"
        private val supportedAlternativeStores = emptyArray<String>()
        private const val IS_PROD = false
    }

    private val deviceStateListener = object : ThreatListener.DeviceState {
        override fun onUnlockedDeviceDetected() {
            Log.d("TAG", "onUnlockedDeviceDetected: ")
        }

        override fun onHardwareBackedKeystoreNotAvailableDetected() {
            Log.d("TAG", "onHardwareBackedKeystoreNotAvailableDetected: ")
        }

        override fun onDeveloperModeDetected() {
            Log.d("TAG", "onDeveloperModeDetected: ")

            Toast.makeText(this@GoldApplication, "onDeveloperModeDetected", Toast.LENGTH_SHORT).show()
            exitProcess(0)

        }

        override fun onSystemVPNDetected() {
            Log.d("TAG", "onSystemVPNDetected: ")

        }
    }

    override fun onCreate() {
        super.onCreate()
        try {

            AppSharedPref.start(this)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        val config = TalsecConfig(
            PACKAGE_NAME,
            expectedSigningCertificateHashBase64,
            MAIL_ADDRESS,
            supportedAlternativeStores,
            IS_PROD
        )

        ThreatListener(this, deviceStateListener).registerListener(this)
        Talsec.start(this, config)

    }

    override fun onRootDetected() {
        Toast.makeText(this, "onRootDetected", Toast.LENGTH_SHORT).show()
        exitProcess(0)
    }

    override fun onDebuggerDetected() {
//        Toast.makeText(this, "onDebuggerDetected", Toast.LENGTH_SHORT).show()
//        exitProcess(0)
    }

    override fun onEmulatorDetected() {
        Toast.makeText(this, "onEmulatorDetected", Toast.LENGTH_SHORT).show()
        exitProcess(0)
    }

    override fun onTamperDetected() {
//        Toast.makeText(this, "onTamperDetected", Toast.LENGTH_SHORT).show()
//        exitProcess(0)
    }

    override fun onUntrustedInstallationSourceDetected() {
//        Toast.makeText(this, "onUntrustedInstallationSourceDetected", Toast.LENGTH_SHORT).show()
//        exitProcess(0)
    }

    override fun onHookDetected() {
//        Toast.makeText(this, "onHookDetected", Toast.LENGTH_SHORT).show()
//        exitProcess(0)
    }

    override fun onDeviceBindingDetected() {
//        Toast.makeText(this, "onDeviceBindingDetected", Toast.LENGTH_SHORT).show()
//        exitProcess(0)
    }

    override fun onObfuscationIssuesDetected() {
//        Toast.makeText(this, "onObfuscationIssuesDetected", Toast.LENGTH_SHORT).show()
//        exitProcess(0)
    }
}