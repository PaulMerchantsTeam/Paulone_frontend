package com.paulmerchants.gold.ui.auth

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.LoginWithMobileMpinBinding
import com.paulmerchants.gold.databinding.OtpFillLayoutDialogBinding
import com.paulmerchants.gold.model.newmodel.ReqLoginWithMpin
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility.changeStatusBarWithReqdColor
import com.paulmerchants.gold.utility.AppUtility.noInternetDialog
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants.CUST_MOBILE
import com.paulmerchants.gold.utility.InternetUtils
import com.paulmerchants.gold.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @since 22/03/2023
 * @author Prithvi Kumar
 *
 * ----Code optimized----
 */

@AndroidEntryPoint
class LoginScreenFrag :
    BaseFragment<LoginWithMobileMpinBinding>(LoginWithMobileMpinBinding::inflate) {
    private var customDialog: androidx.appcompat.app.AlertDialog? = null

    private val loginViewModel: LoginViewModel by viewModels()

    override fun LoginWithMobileMpinBinding.initialize() {
        changeStatusBarWithReqdColor(requireActivity(), R.color.pg_color)
        (activity as MainActivity).locationProvider.startLocationUpdates()
    }

    private fun isValidate(): Boolean {
        if (AppSharedPref.getStringValue(CUST_MOBILE).toString().isEmpty()) {
            return false
        }

        val editTexts =
            listOf(binding.pinOneEt, binding.pinTwoEt, binding.pinThreeEt, binding.pinFourEt)
        for (editText in editTexts) {
            if (editText.text.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun onStart() {
        super.onStart()

        setupMpinEditTextFocus()
        (activity as? MainActivity)?.apply {
           commonViewModel. getUnderMaintenanceStatus()
            checkForDownFromRemoteConfig()
            commonViewModel.isRemoteConfigCheck.observe(viewLifecycleOwner) {
                it?.let {
                    if (it ) {
                        (activity as MainActivity).showUnderMainTainPage()
                    }
                }
            }
        }
        binding.loginWithDifferentAccTv.setOnClickListener {
            AppSharedPref.putBoolean(Constants.OTP_VERIFIED, false)
            findNavController().navigate(R.id.phoenNumVerifiactionFragment)
        }

        binding.fogetMpin.setOnClickListener {
            if (InternetUtils.isNetworkAvailable(requireContext())) {
                val mobileNumber = AppSharedPref.getStringValue(CUST_MOBILE).toString()
                showCustomDialogOTPVerify(
                    mobileNumber
                )
                loginViewModel.getOtp(
                    mobileNumber,
                    (activity as? MainActivity)?.mLocation
                )

            } else {
                noInternetDialog()
            }

        }

        loginViewModel.verifyOtp.observe(viewLifecycleOwner) { verifyOtpResponse ->
            verifyOtpResponse?.let {
                if (it.statusCode == "200") {
                    Log.e("TAG", "onStart: .=======${it.data}")
                    customDialog?.dismiss()
                    loginViewModel.timer?.cancel()
                    loginViewModel.countStr.postValue("")
                    findNavController().navigate(R.id.resetMpinDialog)
                } else {
                    Log.e("TAG", "onStart: .=======${it.data}")
                }
            }
        }
        binding.signUpBtn.setOnClickListener {
            if (InternetUtils.isNetworkAvailable(requireContext())) {
                if (isValidate()) {
                    val pin =
                        "${binding.pinOneEt.text}${binding.pinTwoEt.text}${binding.pinThreeEt.text}${binding.pinFourEt.text}"
                    loginViewModel.loginWithMpin(
                        findNavController(),
                        AppSharedPref,
                        ReqLoginWithMpin(AppSharedPref.getStringValue(CUST_MOBILE).toString(), pin),
                        (activity as? MainActivity)?.mLocation
                    )
                }
            } else {
                noInternetDialog()
            }
        }
        loginViewModel.getTokenResp.observe(viewLifecycleOwner) {
            it?.let {
                if (it.code() == 200 && isValidate()) {
                    val pin =
                        "${binding.pinOneEt.text}${binding.pinTwoEt.text}${binding.pinThreeEt.text}${binding.pinFourEt.text}"
                    loginViewModel.loginWithMpin(
                        findNavController(),
                        AppSharedPref,
                        ReqLoginWithMpin(AppSharedPref.getStringValue(CUST_MOBILE).toString(), pin),
                        (activity as? MainActivity)?.mLocation
                    )
                }
            }
        }
    }


    private fun showCustomDialogOTPVerify(
        mobile: String,
    ) {
        val dialogBinding =
            OtpFillLayoutDialogBinding.inflate(this.layoutInflater)

        setupOtpEditTextFocus(dialogBinding)
        customDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
            .apply { show() }

        loginViewModel.countStr.observe(viewLifecycleOwner) { countStr ->
            countStr?.let {
                dialogBinding.didnotReceiveTv.text = if (it == "00") {
                    getString(R.string.send_again)
                } else {
                    "Didn't receive OTP? $it"
                }
                dialogBinding.didnotReceiveTv.text = getString(R.string.send_again)
                dialogBinding.didnotReceiveTv.setOnClickListener {
                    AppSharedPref.getStringValue(CUST_MOBILE)?.let { mobileNumber ->
                        loginViewModel.getOtp(mobileNumber, (activity as? MainActivity)?.mLocation)
                    }
                }
            }
        }
        customDialog?.setOnDismissListener {
            loginViewModel.timer?.cancel()
        }
        //verify Otp
        dialogBinding.verifyOtpBtn.setOnClickListener {
            if (dialogBinding.otpOneEt.text.isNotEmpty() && dialogBinding.otpTwoEt.text.isNotEmpty() &&
                dialogBinding.otpThreeEt.text.isNotEmpty() && dialogBinding.otpFourEt.text.isNotEmpty()
            ) {
                loginViewModel.verifyOtp(
                    AppSharedPref,
                    mobile,
                    otp = "${dialogBinding.otpOneEt.text}${dialogBinding.otpTwoEt.text}" +
                            "${dialogBinding.otpThreeEt.text}${dialogBinding.otpFourEt.text}",
                    (activity as MainActivity).mLocation
                )
            } else {
                "Please fill Otp".showSnackBar()
            }

            val otpFilled = with(dialogBinding) {
                otpOneEt.text.isNotEmpty() && otpTwoEt.text.isNotEmpty() &&
                        otpThreeEt.text.isNotEmpty() && otpFourEt.text.isNotEmpty()
            }
            if (otpFilled) {
                val otp = "${dialogBinding.otpOneEt.text}${dialogBinding.otpTwoEt.text}" +
                        "${dialogBinding.otpThreeEt.text}${dialogBinding.otpFourEt.text}"
                loginViewModel.verifyOtp(AppSharedPref, mobile, otp, (activity as? MainActivity)?.mLocation)
            } else {
                "Please fill OTP".showSnackBar()
            }
        }
        dialogBinding.cancelDgBtn.setOnClickListener {
            customDialog?.dismiss()
            loginViewModel.timer?.cancel()
            //cancel Otp
        }
    }

    private fun setupEditTextFocusNavigation(vararg editTexts: EditText?) {
        editTexts.forEachIndexed { index, editText ->
            editText?.apply {
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (s?.length == 1 && index < editTexts.size - 1) {
                            editTexts[index + 1]?.requestFocus()
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })

                setOnKeyListener { _, keyCode, event ->
                    if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && index > 0 && text.isEmpty()) {
                        editTexts[index - 1]?.text = null
                        editTexts[index - 1]?.requestFocus()
                        return@setOnKeyListener true
                    }
                    false
                }
            }
        }
    }

    // Usage
    private fun setupMpinEditTextFocus() {
        setupEditTextFocusNavigation(binding.pinOneEt, binding.pinTwoEt, binding.pinThreeEt, binding.pinFourEt)
    }

    private fun setupOtpEditTextFocus(bindingOtp: OtpFillLayoutDialogBinding) {
        setupEditTextFocusNavigation(bindingOtp.otpOneEt, bindingOtp.otpTwoEt, bindingOtp.otpThreeEt, bindingOtp.otpFourEt)
    }

}