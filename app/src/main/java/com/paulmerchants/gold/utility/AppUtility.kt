package com.paulmerchants.gold.utility

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import com.paulmerchants.gold.model.RespClosureReceipt
import com.paulmerchants.gold.model.RespGetCustomer
import com.paulmerchants.gold.model.RespGetLoanOutStanding
import com.paulmerchants.gold.model.RespGetReceipt
import com.paulmerchants.gold.model.RespLoanDueDate
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility.convertStringToJson
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.ShortBufferException
import javax.crypto.spec.SecretKeySpec


object AppUtility {
    private lateinit var dialog: AlertDialog

    fun getFirstName(fullName: String?): String? {
        return fullName?.substringBefore(" ")
    }

    fun getDateFormat(date: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormat = SimpleDateFormat("dd/MM/yyyy")
        val dateC = inputFormat.parse(date)
        val outputDate = dateC?.let { outputFormat.format(it) }
        println("Converted date: $outputDate")
        return outputDate
    }

    fun numberOfDaysWrtCurrent(date: String): Long {
        val daysDifference: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val specificDate = LocalDateTime.parse(date)
            val currentDate = LocalDateTime.now()
            daysDifference = ChronoUnit.DAYS.between(specificDate, currentDate)
            println("The number of days difference is: $daysDifference")
            return daysDifference
        } else {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val specificDate = dateFormat.parse(date)
            val currentDate = Date()
            val calendar1 = Calendar.getInstance()
            val calendar2 = Calendar.getInstance()
            if (specificDate != null) {
                calendar1.time = specificDate
            }
            calendar2.time = currentDate
            val millisecondsDifference = calendar2.timeInMillis - calendar1.timeInMillis
            daysDifference = TimeUnit.MILLISECONDS.toDays(millisecondsDifference)
            println("The number of days difference is: $daysDifference")
            return daysDifference
        }
    }


    inline fun <reified T> convertStringToJson(string: String): T? {
        return try {
            val gson = Gson()
            gson.fromJson(string, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun stringToJson(string: String): RespLogin {
        val gson = Gson()
        return gson.fromJson(string, RespLogin::class.java)
    }

    fun stringToJsonCustomer(string: String): RespGetCustomer {
        val gson = Gson()
        return gson.fromJson(string, RespGetCustomer::class.java)
    }

    fun stringToJsonGetPending(string: String): GetPendingInrstDueResp {
        val gson = Gson()
        return gson.fromJson(string, GetPendingInrstDueResp::class.java)
    }

    fun stringToJsonGetLoanOutstanding(string: String): RespGetLoanOutStanding {
        val gson = Gson()
        return gson.fromJson(string, RespGetLoanOutStanding::class.java)
    }

    fun stringToJsonGetLoanDueDate(string: String): RespLoanDueDate {
        val gson = Gson()
        return gson.fromJson(string, RespLoanDueDate::class.java)
    }

    fun stringToJsonGetLoanClosureReceipt(string: String): RespClosureReceipt {
        val gson = Gson()
        return gson.fromJson(string, RespClosureReceipt::class.java)
    }

    fun stringToJsonGetReceipt(string: String): RespGetReceipt {
        val gson = Gson()
        return gson.fromJson(string, RespGetReceipt::class.java)
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
//    val a = decryptKey(
//        "38665180BC70B97BA443CACF2BFDEE67",
//        "KnxvW5yEeCfAWhhz0yRS4gKd9ENrCE9WAuV99u1jutL2r+M1XnP7V5Vc+p/h1jcsNtsqN3QITsqnFysPwSwOi9LGknlLZCkpcdCUXESdl9V06HDP/D0byNhENEAgBLHSUSlYJ7TgmpAGOn+l+wl6t6Cdu6KyQZHiNbmo0QO+Y47wiyw83OJdrFGHCKK6VhaRCKnITtZkqAxknnExD4DsRV8nTWwm20posgRa62P7RVOKUJAINR0zaI6RqdRHC8bSHit1GOITfSUMs6eFviMn3VqoLx8G7JcME3amZDF/JQWeTaUz09KpCMpLk1ARHO2l2J9+gduLOwYLhRIcwcWQ4SKiW0ArUXmna23k0C/bfU3UrVyPD7KiWH4uBFCenRRahuPWoU+5/6QlA2U4wJbdKw=="
//    )
//    println(a)
//    val j = AppUtility.stringToJson(a.toString())
//    val respLogin: RespLogin? = convertStringToJson(a.toString())
//    println(j)
//    println(respLogin)
//    println(respLogin?.Status)
//    println(respLogin?.JWToken)

    println(AppUtility.getDateFormat("2024-06-20T00:00:00"))
}



