package com.paulmerchants.gold.common


import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.paulmerchants.gold.MainNavGraphDirections
import com.paulmerchants.gold.MyLifecycleObserver
import com.paulmerchants.gold.R
import com.paulmerchants.gold.TimeoutManager
import com.paulmerchants.gold.ui.MainActivity

/**
 * Abstract Activity which binds [ViewModel] [VM] and [ViewBinding] [VB]
 */

abstract class BaseActivity<VM : ViewModel, VB : ViewBinding> : AppCompatActivity(), MyLifecycleObserver.DialogListener {
//    var timeoutManager: TimeoutManager? = null
    protected abstract val mViewModel: VM
    private lateinit var myLifecycleObserver: MyLifecycleObserver
    private var dialog: AlertDialog? = null
    lateinit var binding: VB
    var isDialogOpen: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
          myLifecycleObserver = MyLifecycleObserver(this, listener = this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(myLifecycleObserver)
//        timeoutManager?.stop() // Stop previous timeoutManager if exists
//        // Create a new TimeoutManager instance for the current activity
//        timeoutManager = TimeoutManager(
//            timeoutDuration = 1 * 3 * 1000L // 20 seconds timeout
//        ) {
//            onAppTimeout()
//        }
    }

    /**
     * It returns [VB] which is assigned to mViewBinding and used in [onCreate]
     */
    abstract fun getViewBinding(): VB
    override fun showAutoTimeDisabledDialog() {
        // Inflate the custom dialog layout
        if(!isDialogOpen){
            isDialogOpen = true

        val dialogView = LayoutInflater.from(this).inflate(R.layout.setting_change_dialog, null)

        // Create the dialog
          dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Prevent dialog dismissal on back press
            .create()

        // Get references to the buttons
        val openSettingsButton: Button = dialogView.findViewById(R.id.button_open_settings)
        val exitAppButton: Button = dialogView.findViewById(R.id.button_exit_app)

        // Set onClickListener for the "Open Settings" button
        openSettingsButton.setOnClickListener {
             startActivity(Intent(Settings.ACTION_DATE_SETTINGS))
            dialog?.dismiss() // Dismiss the dialog
             // Close the app after redirecting
            isDialogOpen = false
        }

        // Set onClickListener for the "Exit App" button
        exitAppButton.setOnClickListener {
            dialog?.dismiss() // Dismiss the dialog
            closeApp() // Close the app
        }
            isDialogOpen = false
        // Show the dialog
        dialog?.show()
    }
    }
    private fun closeApp() {
        if (this is Activity) {
            (this as Activity).finishAffinity() // Closes the app
        } else {
            // For non-activity contexts (e.g., in services), use this approach
            System.exit(0)  // Force close the app
        }
    }

    override fun dismissAutoTimeDisabledDialog() {
        isDialogOpen = false
        dialog?.dismiss()

    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
//        timeoutManager?.stop()
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
//        if (isAppInBackground(this)) {
////           onAppTimeout()
//            // Start the timer here
//            Log.d("TAG", "onStop: AppInBackground")
//        }
//        else{
////            timeoutManager?.stop()
//        }
    }
    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
        lifecycle.removeObserver(myLifecycleObserver)
    }
//    fun isAppInBackground(context: Context): Boolean {
//        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        val appProcesses = activityManager.runningAppProcesses ?: return true
//
//        for (appProcess in appProcesses) {
//            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                if (appProcess.processName == context.packageName) {
//                    return false
//                }
//            }
//        }
//        return true
//    }

//    private fun onAppTimeout() {
//        timeoutManager?.stop()  // Stop the timeout manager before navigating
//        val mIntent = Intent(applicationContext,MainActivity::class.java ).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        startActivity(mIntent)
//        finish()
//    }
//    fun getTimeoutManagerInstance(): TimeoutManager {
//        // Return the timeoutManager instance, or throw an exception if not initialized
//        return timeoutManager ?: throw IllegalStateException("TimeoutManager is not initialized.")
//    }
}