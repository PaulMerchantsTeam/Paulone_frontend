package com.paulmerchants.gold.utility

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
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


    @Throws(Exception::class)
    fun encrypt(
        plaintext: ByteArray?,
        key: SecretKey,
        IV  : ByteArray?,
    ): ByteArray? {
        val cipher = Cipher.getInstance("AES")
        val keySpec =
            SecretKeySpec(key.encoded, "AES")
        val ivSpec = IvParameterSpec(IV)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        return cipher.doFinal(plaintext)
    }

    fun decrypt(cipherText: ByteArray?, key: SecretKey, IV: ByteArray?): String? {
        try {
            val cipher = Cipher.getInstance("AES")
            val keySpec = SecretKeySpec(key.encoded, "AES")
            val ivSpec = IvParameterSpec(IV)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decryptedText = cipher.doFinal(cipherText)
            return String(decryptedText)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
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



