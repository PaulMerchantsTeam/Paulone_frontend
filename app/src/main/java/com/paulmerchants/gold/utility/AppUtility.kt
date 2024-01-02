package com.paulmerchants.gold.utility

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfWriter
import com.paulmerchants.gold.BuildConfig
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
import com.paulmerchants.gold.model.newmodel.DeviceDetailsDTO
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility.convertStringToJson
import com.paulmerchants.gold.utility.AppUtility.formatDateFromMilliSec
import com.paulmerchants.gold.utility.AppUtility.getCurrentDate
import com.paulmerchants.gold.utility.AppUtility.getDateWithOrdinals
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.ShortBufferException
import javax.crypto.spec.SecretKeySpec

/**
 * android:background=”?android:attr/selectableItemBackground”: this creates ripple effect with border.
android:background=”?android:attr/selectableItemBackgroundBorderless”: this creates ripple effect without border.
Note: These tags are need to be set under the TextView.
 */

object AppUtility {
    fun dialer(context: Context, number: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL
        intent.data = Uri.parse("tel: $number")
        context.startActivity(intent)
    }

    fun formatDateFromMilliSec(milliSeconds: Long): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat("HH:mm aa, dd MMM, yyyy", Locale.getDefault())
//        HH:mm, dd MMMM, yyyy
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun validateUPI(upi: String): Boolean {
        val upiPatt = Pattern.compile("^[0-9A-Za-z.-]{2,256}@[A-Za-z]{2,64}$")

        // Handling the empty string
        if (upi.isEmpty()) {
            return false
        }

        // Matching the UPI and regular expression
        val matcher: Matcher = upiPatt.matcher(upi)
        return matcher.matches()
    }

    private lateinit var dialog: AlertDialog

    fun ShimmerFrameLayout.showShimmer() {
        startShimmer()
        show()
    }

    fun ShimmerFrameLayout.hideShim() {
        stopShimmer()
        hideShimmer()
        hide()
    }

    fun getDeviceDetails() = DeviceDetailsDTO(
        BuildConfig.VERSION_NAME,
        BuildConfig.VERSION_CODE.toString(),
        Build.MODEL,
        "121212.12",
        "41221.22"
    )

    fun blurTextView(blurredTextView: TextView, context: Context) {
        // Wait until the layout is measured to get the dimensions of the TextView
        blurredTextView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                blurredTextView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Get the dimensions of the TextView
                val width = blurredTextView.width
                val height = blurredTextView.height

                // Create a Bitmap of the TextView's background
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                blurredTextView.background?.draw(canvas)

                // Apply blur effect to the Bitmap
                val blurredBitmap = blurBitmap(bitmap, 25f, context)

                // Create a BitmapDrawable with the blurred Bitmap and set it as the TextView's background
                val blurredDrawable = BitmapDrawable(blurredTextView.resources, blurredBitmap)
                blurredTextView.background = blurredDrawable
            }
        })
    }

    // Function to apply blur effect to a Bitmap
    private fun blurBitmap(bitmap: Bitmap, radius: Float, context: Context): Bitmap {
        val overlay = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(overlay)

        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        val blurMaskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
        paint.maskFilter = blurMaskFilter

        canvas.drawBitmap(overlay, 0f, 0f, paint)

        return overlay
    }

    fun getScreenBitmap(view: View, backgroundColor: Int): Bitmap {
//        view.setBackgroundColor(backgroundColor)
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun saveAsPdf(
        context: Context,
        pdfWidth: Float,
        pdfHeight: Float,
        bitmap: Bitmap,
        backgroundColor: Int,
    ) {
        val document = Document(Rectangle(pdfWidth, pdfHeight))
        // Set the PDF background color
        document.addHeader("Statement", "")
        val outputPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outputFilename = "statement.pdf"
        val outputFile = File(outputPath, outputFilename)

        try {
            val fileOutputStream = FileOutputStream(outputFile)
            PdfWriter.getInstance(document, fileOutputStream)
            document.open()
            // Create a new Rectangle with the desired background color
            val pdfBackgroundColor = BaseColor(backgroundColor)
            val rectangle = Rectangle(document.pageSize)
            rectangle.backgroundColor = pdfBackgroundColor

            // Add the image to the PDF
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val image = Image.getInstance(stream.toByteArray())
            image.scaleToFit(document.pageSize.width, document.pageSize.height)
            document.add(image)

            document.close()
            fileOutputStream.close()

            Toast.makeText(context, "PDF saved to Downloads folder.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save PDF.", Toast.LENGTH_SHORT).show()
        }
    }


    fun getBitmapFromView(view: View, activity: Activity, callback: (Bitmap) -> Unit) {
        activity.window?.let { window ->
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PixelCopy.request(
                        window, Rect(
                            locationOfViewInWindow[0],
                            locationOfViewInWindow[1],
                            locationOfViewInWindow[0] + view.width,
                            locationOfViewInWindow[1] + view.height
                        ), bitmap, { copyResult ->
                            if (copyResult == PixelCopy.SUCCESS) {
                                callback(bitmap)
                            }
                            // possible to handle other result codes ...
                        }, Handler()
                    )
                }
            } catch (e: IllegalArgumentException) {
                // PixelCopy may throw IllegalArgumentException, make sure to handle it
                e.printStackTrace()
            }
        }
    }

    fun getFirstName(fullName: String?): String? {
        return fullName?.first().toString()
    }

    fun getDateWithOrdinals(inputDate: String): String { //14th May
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMMM", Locale.getDefault())

//        val inputDate = "2027-07-16T00:00:00"
        val date = inputFormat.parse(inputDate)
        val outputDate = date?.let { outputFormat.format(it) }

        val day = date.date
        val ordinal = getDayOrdinal(day)
        println("Converted date: $ordinal $outputDate")
        return "$ordinal $outputDate"
    }

    fun getDateWithYearOrdinals(inputDate: String): String? { //14th May, 2023
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) //14th May
        val outputFormat = SimpleDateFormat("d MMM, yyyy", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        val formattedDate = date?.let { formatWithOrdinalSuffix(it, outputFormat) }
        println("Current Date: $formattedDate")
        return formattedDate
    }


    fun getYear(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy", Locale.getDefault())

//        val inputDate = "2027-07-16T00:00:00"
        val date = inputFormat.parse(inputDate)
        val outputDate = date?.let { outputFormat.format(it) }
        println("Converted date:$outputDate")
        return outputDate.toString()
    }


    fun getDayOrdinal(day: Int): String {
        return when (day) {
            1, 21, 31 -> "${day}st"
            2, 22 -> "${day}nd"
            3, 23 -> "${day}rd"
            else -> "${day}th"
        }
    }

    fun getOrdinalSuffix(day: Int): String {
        return when (day % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

    fun getCurrentDate(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().toString()
        } else {
            Date().toString()
        }
    }

    fun getCurrentDateOnly(): String {
        val outputFormat = SimpleDateFormat("d MMM, yyyy", Locale.getDefault())
        val currentDate = Date()
        val formattedDate = formatWithOrdinalSuffix(currentDate, outputFormat)
        println("Current Date: $formattedDate")
        return formattedDate
    }


    fun formatWithOrdinalSuffix(date: Date, format: SimpleDateFormat): String {
        val day = SimpleDateFormat("d", Locale.getDefault()).format(date).toInt()
        val suffix = getOrdinalSuffix(day)

        return format.format(date).replaceFirst("\\d+".toRegex(), "$0$suffix")
    }

    fun getDateMoth(inputDate: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM d", Locale.getDefault())

//        val inputDate = "2027-04-16T00:00:00"
        val date = inputFormat.parse(inputDate)
        val outputDate = date?.let { outputFormat.format(it) }
        println("Converted date: $outputDate")
        return outputDate
    }

    fun takeScreenShot(view: View, context: Context) {

        //This is used to provide file name with Date a format
        val date = Date()
        val format: CharSequence = DateFormat.format("MM-dd-yyyy_hh:mm:ss", date)

        //It will make sure to store file to given below Directory and If the file Directory dosen't exist then it will create it.
        try {
            val mainDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FilShare"
            )
            if (!mainDir.exists()) {
                val mkdir = mainDir.mkdir()
            }

            //Providing file name along with Bitmap to capture screenview
            val path = "$mainDir/qrcode-$format.jpeg"
            view.isDrawingCacheEnabled = true
            val bitmap: Bitmap? = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false

            //This logic is used to save file at given location with the given filename and compress the Image Quality.
            val imageFile = File(path)
            val fileOutputStream = FileOutputStream(imageFile)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            //Create New Method to take ScreenShot with the imageFile.
            shareScreenShot(imageFile, context)
        } catch (e: IOException) {
            Log.d("TAG", e.message!!)

        }
    }

    private fun shareScreenShot(imageFile: File, context: Context) {

        //Using sub-class of Content provider
        val uri: Uri = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID.toString() + ".provider",
            imageFile
        )

        //Explicit intent
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
        val mArrayList: ArrayList<String> = ArrayList<String>()
        mArrayList.add("Paul One:")
        mArrayList.add("App - https://play.google.com/store/apps/details?id=com.paulmerchants.gold")
        intent.putExtra(Intent.EXTRA_TEXT, TextUtils.join("\n", mArrayList))
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        //It will show the application which are available to share Image; else Toast message will throw.
        try {
            context.startActivity(Intent.createChooser(intent, "Share With"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No App Available", Toast.LENGTH_SHORT).show()
        }
    }

    fun getDateFormat(date: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("dd/MM/yyyy")
        val dateC = inputFormat.parse(date)
        val outputDate = dateC?.let { outputFormat.format(it) }
        println("Converted date: $outputDate")
        return outputDate
    }

    fun Fragment.showSnackBar(message: String) {
        val snakbar = this.view?.let {
            Snackbar.make(
                it,
                message,
                Snackbar.LENGTH_SHORT
            )
        }
        val snackbarView = snakbar?.view
        val params = snackbarView?.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        snackbarView.layoutParams = params
        snakbar.show()
    }

    fun BottomSheetDialogFragment.showSnackBar(message: String) {
        val snakbar = this.view?.let {
            Snackbar.make(
                it,
                message,
                Snackbar.LENGTH_SHORT
            )
        }
        val snackbarView = snakbar?.view
        val params = snackbarView?.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        snackbarView.layoutParams = params
        snakbar.show()
    }

    // Helper function to convert dp to pixels
    fun convertDpToPx(dp: Int): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }


    /**
     *
     * Show Snack Bar
     * **/
    /*   fun String?.showSnackBar() = try {
           var msg = this
   //    if (msg?.contains("Unable to resolve host") == true || msg?.contains(
   //            "Failed to connect", true
   //        ) == true
   //    ) msg = MainActivity.context.get()?.getString(R.string.internet_not_connected)
           Snackbar.make(
               (MainActivity.context.get() as Activity).findViewById(android.R.id.content),
               msg ?: "Something went wrong.",
               Snackbar.LENGTH_LONG
           ).apply {
               setBackgroundTint(
                   ContextCompat.getColor(
                       (MainActivity.context.get() as Activity), R.color.splash_screen_three
                   )
               )
               animationMode = Snackbar.ANIMATION_MODE_SLIDE
               setTextColor(
                   ContextCompat.getColor(
                       (MainActivity.context.get() as Activity), R.color.white
                   )
               )
               view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5
               show()
           }


       } catch (e: Exception) {
           e.printStackTrace()
       }*/

    fun String?.showSnackBar() = try {
        val msg = this ?: "Something went wrong."
        val activity = MainActivity.context.get() as Activity

        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            msg,
            Snackbar.LENGTH_LONG
        )

        // Customize Snackbar layout to show at the top
        val params = snackbar.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.setMargins(
            convertDpToPx(20),
            convertDpToPx(35),
            convertDpToPx(20),
            0
        ) // Adjust top margin as needed
        snackbar.view.layoutParams = params

        snackbar.apply {
            setBackgroundTint(ContextCompat.getColor(activity, R.color.splash_screen_three))
            animationMode = Snackbar.ANIMATION_MODE_FADE
            setTextColor(ContextCompat.getColor(activity, R.color.white))
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5
            show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
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

    fun Context.colorList(id: Int): ColorStateList {
        return ColorStateList.valueOf(ContextCompat.getColor(this, id))
    }

    fun addDrawableGradient(context: Context, layout: View, colors: IntArray) {
        // Create a GradientDrawable for the gradient background
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, // Set your gradient orientation
            colors
        )
        // Optionally, set gradient corners and other properties
//        gradientDrawable.cornerRadius = resources.getDimension(R.dimen._6sdp) // Set corner radius

        // Set the created GradientDrawable as the background of the MaterialCardView
        layout.background = gradientDrawable
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
            context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) == 1
    }

    fun isUsbDebuggingEnabled(context: Context): Boolean {
        return Settings.Global.getInt(
            context.contentResolver, Settings.Global.ADB_ENABLED, 0
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
        val text = "<font color=#3F72AF>$first</font> <font color=#150750>$second</font>"
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
        val input = org.bouncycastle.util.encoders.Base64.decode(strToDecrypt?.trim { it <= ' ' }
            ?.toByteArray(charset("UTF8")))

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
//    getDateWithOrdinals()
    println(getCurrentDate())
//    println(AppUtility.getDateFormat("2024-06-20T00:00:00"))
}



