package com.paulmerchants.gold.ui.auth

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.PhoneAuthFragmentBinding
import com.paulmerchants.gold.utility.hideView
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoenNumVerifiactionFragment :
    BaseFragment<PhoneAuthFragmentBinding>(PhoneAuthFragmentBinding::inflate) {
    private var isMobileEntered: Boolean = false
    override fun PhoneAuthFragmentBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        binding.proceedAuthBtn.setOnClickListener {
            if (!isMobileEntered) {
                if (binding.etPhoenNum.text.isNotEmpty()) {
                    hideAndShowOtpView()
                    isMobileEntered = true
                }
            } else {
                if (binding.otpOneEt.text.isNotEmpty() &&
                    binding.otpTwoEt.text.isNotEmpty() &&
                    binding.otpThreeEt.text.isNotEmpty() &&
                    binding.otpFourEt.text.isNotEmpty()
                ) {
                    hideAndShowProgressView()
                }
            }

        }
    }

    private fun hideAndShowProgressView() {
        binding.apply {
            titleWelcomTv.text = getString(R.string.give_us_some)
            fillOtpParent.hideView()
            mainPgCons.show()
            proceedAuthBtn.hideView()
        }
    }


    private fun hideAndShowOtpView() {
        binding.apply {
            enterPhoneNumMain.hideView()
            callOtpNextFocus()
            fillOtpParent.show()
            pleaseOtpTv.text =
                "${getString(R.string.please_fill_the_otp)} +91${binding.etPhoenNum.text}. ${
                    getString(
                        R.string.change_q
                    )
                }"
        }
    }

    fun callOtpNextFocus() {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
//first parameter is the current EditText and second parameter is next EditText
        binding.otpOneEt.addTextChangedListener(
            GenericTextWatcher(
                binding.otpOneEt,
                binding.otpTwoEt
            )
        )
        binding.otpTwoEt.addTextChangedListener(
            GenericTextWatcher(
                binding.otpTwoEt,
                binding.otpThreeEt
            )
        )
        binding.otpThreeEt.addTextChangedListener(
            GenericTextWatcher(
                binding.otpThreeEt,
                binding.otpFourEt
            )
        )
        binding.otpFourEt.addTextChangedListener(GenericTextWatcher(binding.otpFourEt, null))

//GenericKeyEvent here works for deleting the element and to switch back to previous EditText
//first parameter is the current EditText and second parameter is previous EditText
        binding.otpOneEt.setOnKeyListener(GenericKeyEvent(binding.otpOneEt, null))
        binding.otpTwoEt.setOnKeyListener(GenericKeyEvent(binding.otpTwoEt, binding.otpOneEt))
        binding.otpThreeEt.setOnKeyListener(GenericKeyEvent(binding.otpThreeEt, binding.otpTwoEt))
        binding.otpFourEt.setOnKeyListener(GenericKeyEvent(binding.otpFourEt, binding.otpThreeEt))
    }

    inner class GenericKeyEvent internal constructor(
        private val currentView: EditText,
        private val previousView: EditText?
    ) : View.OnKeyListener {
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.otpOneEt && currentView.text.isEmpty()) {
                //If current is empty then previous EditText's number will also be deleted
                previousView?.text = null
                previousView?.requestFocus()
                return true
            }
            return false
        }


    }

    inner class GenericTextWatcher internal constructor(
        private val currentView: View,
        private val nextView: View?
    ) :
        TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (currentView.id) {
                R.id.otpOneEt -> if (text.length == 1) nextView?.requestFocus()
                R.id.otpTwoEt -> if (text.length == 1) nextView?.requestFocus()
                R.id.otpThreeEt -> if (text.length == 1) nextView?.requestFocus()
                R.id.otpFourEt -> if (text.length == 1) nextView?.requestFocus()
                //You can use EditText4 same as above to hide the keyboard
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {
        }

        override fun onTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {
        }

    }

}
