package com.paulmerchants.gold.utility

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import com.google.gson.Gson
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.ProgressLayoutBinding
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.model.GetPendingInrstDueResp
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.RespGetCustomer
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.ui.MainActivity
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.ShortBufferException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object AppUtility {
    private lateinit var dialog: AlertDialog

    fun stringToJson(string: String): RespLogin {
        val gson = Gson()
        val data = gson.fromJson(string, RespLogin::class.java)
        return data
    }

    fun stringToJsonCustomer(string: String): RespGetCustomer {
        val gson = Gson()
        val data = gson.fromJson(string, RespGetCustomer::class.java)
        return data
    }

    fun stringToJsonGetPending(string: String): GetPendingInrstDueResp {
        val gson = Gson()
        val data = gson.fromJson(string, GetPendingInrstDueResp::class.java)
        return data
    }

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

    /**
     * Progress Bar Layout
     * */

    fun progressBarAlert() = try {
        hideProgressBar()
        MainActivity.context.get()?.let {
            val builder = AlertDialog.Builder(it)
            val layout = ProgressLayoutBinding.inflate(LayoutInflater.from(it))
            builder.setCancelable(false)
            builder.setView(layout.root)
            dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }


    /** Hide Progress Bar */
    fun hideProgressBar() {
        try {
            if (::dialog.isInitialized) dialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

fun decryptKey(key: String, strToDecrypt: String?): String? {
    Security.addProvider(BouncyCastleProvider())
    var keyBytes: ByteArray

    try {
        keyBytes = key.toByteArray(charset("UTF8"))
        val skey = SecretKeySpec(keyBytes, "AES")
        val input = org.bouncycastle.util.encoders.Base64
            .decode(strToDecrypt?.trim { it <= ' ' }?.toByteArray(charset("UTF8")))

        synchronized(Cipher::class.java) {
            val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
            cipher.init(Cipher.DECRYPT_MODE, skey)

            val plainText = ByteArray(cipher.getOutputSize(input.size))
            var ptLength = cipher.update(input, 0, input.size, plainText, 0)
            ptLength += cipher.doFinal(plainText, ptLength)
            val decryptedString = String(plainText)
            return decryptedString.trim { it <= ' ' }
        }
    } catch (uee: UnsupportedEncodingException) {
        uee.printStackTrace()
    } catch (ibse: IllegalBlockSizeException) {
        ibse.printStackTrace()
    } catch (bpe: BadPaddingException) {
        bpe.printStackTrace()
    } catch (ike: InvalidKeyException) {
        ike.printStackTrace()
    } catch (nspe: NoSuchPaddingException) {
        nspe.printStackTrace()
    } catch (nsae: NoSuchAlgorithmException) {
        nsae.printStackTrace()
    } catch (e: ShortBufferException) {
        e.printStackTrace()
    }

    return null
}


fun main() {
    val a = decryptKey(
        "38665180BC70B97BA443CACF2BFDEE67",
        "KnxvW5yEeCfAWhhz0yRS4gKd9ENrCE9WAuV99u1jutL2r+M1XnP7V5Vc+p/h1jcsNtsqN3QITsqnFysPwSwOi9LGknlLZCkpcdCUXESdl9V06HDP/D0byNhENEAgBLHSUSlYJ7TgmpAGOn+l+wl6t6Cdu6KyQZHiNbmo0QO+Y47wiyw83OJdrFGHCKK6VhaRCKnITtZkqAxknnExD4DsRV8nTWwm20posgRa62P7RVOKUJAINR0zaI6RqdRHC8bSHit1GOITfSUMs6eFviMn3VqoLx8G7JcME3amZDF/JQWeTaUz09KpCMpLk1ARHO2l2J9+gduLOwYLhRIcwcWQ4SKiW0ArUXmna23k0C/bfU3UrVyPD7KiWH4uBFCenRRahuPWoU+5/6QlA2U4wJbdKw=="
    )
    println(a)
    val j = AppUtility.stringToJson(a.toString())
    println(j)
}



