package com.paulmerchants.gold.common

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.paulmerchants.gold.MyLifecycleObserver
import com.paulmerchants.gold.R


open class BaseFragment< T : ViewBinding>(private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> T) :
    Fragment(),MyLifecycleObserver.DialogListener {
    private var isDialogOpen = false
    private var autoTimeDialog: AlertDialog? = null

        private lateinit var myLifecycleObserver:MyLifecycleObserver
    private var _binding: T? = null

    // This can be accessed by the child fragments
    // Only valid between onCreateView and onDestroyView
    val binding: T get() = _binding!!

    // Make it open, so it can be overridden in child fragments
    open fun T.initialize() {

    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//          myLifecycleObserver = MyLifecycleObserver(requireContext(),this)
//        ProcessLifecycleOwner.get().lifecycle.addObserver(myLifecycleObserver)
//    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = inflateMethod.invoke(inflater, container, false)

        // Calling the extension function
        binding.initialize()
        myLifecycleObserver = MyLifecycleObserver(requireContext(), this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(myLifecycleObserver)
        // replaced _binding!! with binding
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Initialize and register the ContentObserver

    }

    override fun onStop() {
        super.onStop()
        autoTimeDialog?.dismiss()
        isDialogOpen = false
    }




    override fun showAutoTimeDisabledDialog() {
        // Inflate the custom dialog layout
        if (!isDialogOpen){
            isDialogOpen = true

        val dialogView = LayoutInflater.from(context).inflate(R.layout.setting_change_dialog, null)

        // Create the dialog
          autoTimeDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false) // Prevent dialog dismissal on back press
            .create()

        // Get references to the buttons
        val openSettingsButton: Button = dialogView.findViewById(R.id.button_open_settings)
        val exitAppButton: Button = dialogView.findViewById(R.id.button_exit_app)

        // Set onClickListener for the "Open Settings" button
        openSettingsButton.setOnClickListener {
            requireContext().startActivity(Intent(Settings.ACTION_DATE_SETTINGS))
            autoTimeDialog?.dismiss() // Dismiss the dialog
            isDialogOpen = false
        }

        // Set onClickListener for the "Exit App" button
        exitAppButton.setOnClickListener {
            autoTimeDialog?.dismiss() // Dismiss the dialog
            isDialogOpen = false
            closeApp() // Close the app
        }

        // Show the dialog
        autoTimeDialog?.show()
    }
    }

    override fun dismissAutoTimeDisabledDialog() {
        autoTimeDialog?.dismiss()
        isDialogOpen = false
    }

    private fun closeApp() {
        if (context is Activity) {
            (context as Activity).finishAffinity() // Closes the app
        } else {
            // For non-activity contexts (e.g., in services), use this approach
            System.exit(0)  // Force close the app
        }
    }

    override fun onPause() {
        super.onPause()
        autoTimeDialog?.dismiss()
        isDialogOpen = false
    }

    override fun onDetach() {
        super.onDetach()
        autoTimeDialog?.dismiss()
        lifecycle.removeObserver(myLifecycleObserver)
    }
}