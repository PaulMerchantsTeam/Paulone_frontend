package com.paulmerchants.gold.utility

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.model.ActionItem
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object AppUtility {

    fun isDeveloperOptionsEnabled(context: Context): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) == 1
    }

    fun isUsbDebuggingEnabled(context: Context): Boolean {
        return Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.ADB_ENABLED, 0
        ) == 1
    }

    fun changeStatusBarWithReqdColor(activity: Activity, colorId: Int) {
        val window = activity.window
        window.statusBarColor = ContextCompat.getColor(activity, colorId)
        val nightModeFlags =
            activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun diffColorText(
        first: String,
        second: String,
        third: String,
        fourth: String = "",
        fifth: String = "",
        sixth: String = "",
        tv: TextView,
    ) {
        val text =
            "<font color=#3F72AF>$first</font> <font color=#150750>$second</font> <font color=#3F72AF>$third</font> <font color=#150750>$fourth</font> <font color=#3F72AF>$fifth</font> <font color=#150750>$sixth</font>"
        tv.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun diffColorText(first: String, second: String, tv: TextView) {
        val text =
            "<font color=#3F72AF>$first</font> <font color=#150750>$second</font>"
        tv.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun onBillClicked(actionItem: ActionItem, navController: NavController) {
        val bundleHomeLoan = Bundle().apply {
            putInt(Constants.BBPS_TYPE, actionItem.itemId)
        }
        navController.navigate(R.id.billsFragment, bundleHomeLoan)
    }

}



