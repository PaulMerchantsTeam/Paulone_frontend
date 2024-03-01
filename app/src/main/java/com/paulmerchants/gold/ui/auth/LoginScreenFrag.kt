package com.paulmerchants.gold.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
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
import com.paulmerchants.gold.utility.AppUtility.noInternetDialog
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants.CUST_MOBILE
import com.paulmerchants.gold.utility.InternetUtils
import com.paulmerchants.gold.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginScreenFrag :
    BaseFragment<LoginWithMobileMpinBinding>(LoginWithMobileMpinBinding::inflate) {
    private var customDialog: androidx.appcompat.app.AlertDialog? = null

    private val loginViewModel: LoginViewModel by viewModels()

    override fun LoginWithMobileMpinBinding.initialize() {

    }

    private fun isValidate(): Boolean = when {
        binding.etPhoenNum.text.toString().isEmpty() -> {
            false
        }

        binding.pinOneEt.text.isEmpty() -> {
            false
        }

        binding.pinTwoEt.text.isEmpty() -> {
            false
        }

        binding.pinThreeEt.text.isEmpty() -> {
            false
        }

        binding.pinFourEt.text.isEmpty() -> {
            false
        }

        else -> {
            true
        }
    }

    override fun onStart() {
        super.onStart()
        callMpinNextFocus()
        binding.etPhoenNum.isEnabled = false
        binding.etPhoenNum.setText(
            AppSharedPref.getStringValue(CUST_MOBILE)
                .toString()
        )
        binding.loginWithDifferentAccTv.setOnClickListener {
            Toast.makeText(requireContext(), "CLICKED", Toast.LENGTH_SHORT).show()
            AppSharedPref.putBoolean(Constants.OTP_VERIFIED, false)
//            findNavController().popBackStack(R.id.loginScreenFrag, true)
            findNavController().navigate(R.id.phoenNumVerifiactionFragment)
        }

        binding.fogetMpin.setOnClickListener {
            if (InternetUtils.isNetworkAvailable(requireContext())) {
                showCustomDialogOTPVerify(
                    AppSharedPref?.getStringValue(
                        CUST_MOBILE
                    ).toString(),
                    title = "OTP send to the number ${
                        AppSharedPref?.getStringValue(
                            CUST_MOBILE
                        )
                    }"
                )
                AppSharedPref?.getStringValue(
                    CUST_MOBILE
                )?.let { loginViewModel.getOtp( it) }
            } else {
                noInternetDialog()
            }

        }

        loginViewModel.verifyOtp.observe(viewLifecycleOwner) {
            it?.let {
                if (it.statusCode == "200") {
                    Log.e("TAG", "onStart: .=======${it.data}")
                    customDialog?.dismiss()
                    loginViewModel.timer?.cancel()
                    loginViewModel.countStr.postValue("")
                  /*  val bundle = Bundle().apply {
                        putBoolean(
                            com.paulmerchants.gold.utility.Constants.IS_RESET_MPIN_FROM_LOGIN_PAGE,
                            true
                        )
                    }*/
                    /**
                     * need to handle in the ResetMpin Page......
                     *
                     *
                     */
                    findNavController().navigate(R.id.resetMpinDialog)
                } else {
                    Log.e("TAG", "onStart: .=======${it.data}")
                }
            }
        }
        binding.signUpBtn.setOnClickListener {
            if (InternetUtils.isNetworkAvailable(requireContext())) {
                if (isValidate()) {
                    loginViewModel.loginWithMpin(
                        findNavController(),
                        AppSharedPref,
                        ReqLoginWithMpin(
                            binding.etPhoenNum.text.toString(),
                            "${binding.pinOneEt.text}${binding.pinTwoEt.text}${binding.pinThreeEt.text}${binding.pinFourEt.text}"
                        )
                    )
                }
            } else {
                noInternetDialog()
            }

        }

        loginViewModel.getTokenResp.observe(viewLifecycleOwner) {
            it?.let {
                if (it.code() == 200) {
                    if (isValidate()) {
                        loginViewModel.loginWithMpin(
                            findNavController(),
                            AppSharedPref,
                            ReqLoginWithMpin(
                                binding.etPhoenNum.text.toString(),
                                "${binding.pinOneEt.text}${binding.pinTwoEt.text}${binding.pinThreeEt.text}${binding.pinFourEt.text}"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun showCustomDialogOTPVerify(
        mobile: String,
        type: Int = 0,
        title: String = "",
    ) {
        val dialogBinding =
            OtpFillLayoutDialogBinding.inflate(this.layoutInflater)

        currentMpinFocus(dialogBinding)
        customDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).create()
        customDialog?.apply {
            setView(dialogBinding.root)
            setCancelable(false)
        }?.show()
        loginViewModel.countStr.observe(viewLifecycleOwner) {
            it?.let {
                if (it == "00") {
                    dialogBinding.didnotReceiveTv.text = getString(R.string.send_again)
                    dialogBinding.didnotReceiveTv.setOnClickListener {
                        AppSharedPref?.getStringValue(
                            CUST_MOBILE
                        )?.let {
                            loginViewModel.getOtp(
                                it
                            )
                        }
                    }
                } else {
                    dialogBinding.didnotReceiveTv.text = "Didn't receive OTP? $it"
                }
            }
        }
        customDialog?.setOnDismissListener { dgInterface ->
            loginViewModel.timer?.cancel()
        }

        dialogBinding.verifyOtpBtn.setOnClickListener {
            if (dialogBinding.otpOneEt.text.isNotEmpty() && dialogBinding.otpTwoEt.text.isNotEmpty() &&
                dialogBinding.otpThreeEt.text.isNotEmpty() && dialogBinding.otpFourEt.text.isNotEmpty()
            ) {
                loginViewModel.verifyOtp(
                    AppSharedPref,
                    mobile,
                    otp = "${dialogBinding.otpOneEt.text}${dialogBinding.otpTwoEt.text}" +
                            "${dialogBinding.otpThreeEt.text}${dialogBinding.otpFourEt.text}"
                )
            } else {
                "Please fill Otp".showSnackBar()
            }
            //verify Otp
        }

        dialogBinding.cancelDgBtn.setOnClickListener {
            customDialog?.dismiss()
            loginViewModel.timer?.cancel()
            //cancel Otp
        }
    }


    private fun callMpinNextFocus() {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        binding.pinOneEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinOneEt, binding.pinTwoEt
            )
        )
        binding.pinTwoEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinTwoEt, binding.pinThreeEt
            )
        )
        binding.pinThreeEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinThreeEt, binding.pinFourEt
            )
        )
        binding.pinFourEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinFourEt, null
            )
        )

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        binding.pinOneEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinOneEt, null
            )
        )
        binding.pinTwoEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinTwoEt, binding.pinOneEt
            )
        )
        binding.pinThreeEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinThreeEt, binding.pinTwoEt
            )
        )
        binding.pinFourEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinFourEt, binding.pinThreeEt
            )
        )
    }

    inner class GenericTextWatcher internal constructor(
        private val currentView: View, private val nextView: View?,
    ) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (currentView.id) {
                R.id.pinOneEt -> {
                    if (text.length == 1) {
//                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                        binding.proceedAuthBtn.disableButton(requireContext())
                    }
                }

                R.id.pinTwoEt -> {
                    if (text.length == 1) {
//                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                        binding.proceedAuthBtn.disableButton(requireContext())
                    }
                }

                R.id.pinThreeEt -> {
                    if (text.length == 1) {
//                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                        binding.proceedAuthBtn.disableButton(requireContext())
                    }
                }

                R.id.pinFourEt -> {
                    if (text.length == 1) {
//                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                        binding.proceedAuthBtn.disableButton(requireContext())
                    }
                }

                R.id.otpOneEt -> {
                    if (text.length == 1) {
                        nextView?.requestFocus()
                    } else {

                    }
                }

                R.id.otpTwoEt -> {
                    if (text.length == 1) {
                        nextView?.requestFocus()
                    } else {
                    }
                }

                R.id.otpThreeEt -> {
                    if (text.length == 1) {
                        nextView?.requestFocus()
                    } else {
                    }
                }

                R.id.otpFourEt -> {
                    if (text.length == 1) {
                        nextView?.requestFocus()
                    } else {
                    }
                }
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int,
        ) {
        }

        override fun onTextChanged(
            arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int,
        ) {
        }

    }

    inner class GenericKeyEvent internal constructor(
        private val currentView: EditText, private val previousView: EditText?,
    ) : View.OnKeyListener {
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.mpinOneEt && currentView.text.isEmpty()) {
                //If current is empty then previous EditText's number will also be deleted
                previousView?.text = null
                previousView?.requestFocus()
                return true
            }
            return false
        }
    }


    private fun currentMpinFocus(bindingOtp: OtpFillLayoutDialogBinding) {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        bindingOtp.otpOneEt.addTextChangedListener(
            GenericTextWatcher(
                bindingOtp.otpOneEt, bindingOtp.otpTwoEt
            )
        )
        bindingOtp.otpTwoEt.addTextChangedListener(
            GenericTextWatcher(
                bindingOtp.otpTwoEt, bindingOtp.otpThreeEt
            )
        )
        bindingOtp.otpThreeEt.addTextChangedListener(
            GenericTextWatcher(
                bindingOtp.otpThreeEt, bindingOtp.otpFourEt
            )
        )
        bindingOtp.otpFourEt.addTextChangedListener(
            GenericTextWatcher(
                bindingOtp.otpFourEt,
                null
            )
        )

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        bindingOtp.otpOneEt.setOnKeyListener(GenericKeyEvent(bindingOtp.otpOneEt, null))
        bindingOtp.otpTwoEt.setOnKeyListener(
            GenericKeyEvent(
                bindingOtp.otpTwoEt,
                bindingOtp.otpOneEt
            )
        )
        bindingOtp.otpThreeEt.setOnKeyListener(
            GenericKeyEvent(
                bindingOtp.otpThreeEt,
                bindingOtp.otpTwoEt
            )
        )
        bindingOtp.otpFourEt.setOnKeyListener(
            GenericKeyEvent(
                bindingOtp.otpFourEt,
                bindingOtp.otpThreeEt
            )
        )
    }


}