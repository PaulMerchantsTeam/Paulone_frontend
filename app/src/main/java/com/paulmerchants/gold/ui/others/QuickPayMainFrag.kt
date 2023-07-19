package com.paulmerchants.gold.ui.others

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.CheckOutWindowFrPaymentBinding
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.ui.auth.PhoenNumVerifiactionFragment
import com.paulmerchants.gold.utility.disableButton
import com.paulmerchants.gold.utility.enableButton
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class QuickPayMainFrag :
    BaseFragment<CheckOutWindowFrPaymentBinding>(CheckOutWindowFrPaymentBinding::inflate) {

    override fun CheckOutWindowFrPaymentBinding.initialize() {
        (activity as MainActivity).changeHeader(
            binding.headerCheckOutQuickPay, getString(R.string.checkout), R.drawable.quest_circle
        )

    }

    override fun onStart() {
        super.onStart()
        callMpinConfirmNextFocus()
        navigateToOtherScrn()
    }

    private fun navigateToOtherScrn() {
        binding.apply {
            confirmBtn.setOnClickListener {
                binding.apply {
                    checkOutParent.hide()
                    lifecycleScope.launchWhenCreated {
                        loadingParent.show()
                        delay(2000)
                        loadingParent.hide()
                        parent.setBackgroundResource(R.color.color_prim_one_40)
                        delay(500)
                        parent.setBackgroundResource(R.color.splash_screen_one)
                        delay(2000)
                        binding.payConfirmedParentAnim.show()
                        delay(2000)
                        findNavController().navigate(R.id.confirmPaymentDueFrag)
                    }
                }
            }
        }
    }

    private fun showSuccessDialog() {

    }

    private fun callMpinConfirmNextFocus() {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        binding.quickPay.otpOneEt.addTextChangedListener(
            GenericTextWatcher(
                binding.quickPay.otpOneEt, binding.quickPay.otpTwoEt
            )
        )
        binding.quickPay.otpTwoEt.addTextChangedListener(
            GenericTextWatcher(
                binding.quickPay.otpTwoEt, binding.quickPay.otpThreeEt
            )
        )
        binding.quickPay.otpThreeEt.addTextChangedListener(
            GenericTextWatcher(
                binding.quickPay.otpThreeEt, binding.quickPay.otpFourEt
            )
        )
        binding.quickPay.otpFourEt.addTextChangedListener(
            GenericTextWatcher(
                binding.quickPay.otpFourEt, null
            )
        )
    }

    inner class GenericTextWatcher internal constructor(
        private val currentView: View, private val nextView: View?,
    ) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (currentView.id) {
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
                //You can use EditText4 same as above to hide the keyboard
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

}