package com.paulmerchants.gold.ui.auth

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.paulmerchants.gold.MainActivity
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.PhoneAuthFragmentBinding
import com.paulmerchants.gold.utility.AppUtility.changeStatusBarWithReqdColor
import com.paulmerchants.gold.utility.AppUtility.diffColorText
import com.paulmerchants.gold.utility.hideView
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.utility.showVg
import com.paulmerchants.gold.utility.showViewWithAnim
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PhoenNumVerifiactionFragment :
    BaseFragment<PhoneAuthFragmentBinding>(PhoneAuthFragmentBinding::inflate) {
    private var isMobileEntered: Boolean = false

    override fun PhoneAuthFragmentBinding.initialize() {
        changeStatusBarWithReqdColor(requireActivity(), R.color.splash_screen_two)
    }

    override fun onStart() {
        super.onStart()
        //Welcome to Paul Gold,
        //we are happy to serve you!!
        diffColorText(
            "Welcome to Paul Gold,\nwe are",
            "happy",
            "to serve you!!",
            binding.titleWelcomTv
        )
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
                    lifecycleScope.launch {
                        delay(2000)
                        binding.mainPgCons.cirStreakTimePg.apply {
                            isIndeterminate = false
                            progress = 100
                        }
                        binding.mainPgCons.progessTv.apply {
                            text = getString(R.string.verified)
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.green_verified
                                )
                            )

                        }
                        delay(1000)
                    }
                    hideAndShowSignUpScreen()
                }
            }

        }

        binding.etPhoenNum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length == 10) {
                    binding.proceedAuthBtn.isEnabled = true
                    binding.proceedAuthBtn.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.splash_screen_one
                        )
                    )
                } else {
                    binding.proceedAuthBtn.isEnabled = false
                    binding.proceedAuthBtn.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_prim_one_40
                        )
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun hideAndShowSignUpScreen() {
        (activity as MainActivity).commonViewModel.timer?.cancel()
        binding.fillOtpParent.hideView()
        binding.signUpParentMain.root.show()
        customizeText()

    }

    private fun customizeText() {
        diffColorText(
            "Please",
            "create",
            "an account before we proceed.",
            binding.signUpParentMain.createAccTitleTv
        )
        diffColorText(
            getString(R.string.already_have_an_acc),
            getString(R.string.log_in),
            binding.signUpParentMain.alreadyHaveAccTv
        )
    }

    private fun hideAndShowProgressView() {
        //Give us some moment to verify few things. Much appreciated.
        diffColorText(
            "Give us some moment to",
            "verify",
            "few things. Much appreciated",
            binding.titleWelcomTv
        )
        binding.apply {
            fillOtpParent.hideView()
            mainPgCons.root.show()
            proceedAuthBtn.hideView()
        }
    }

    private fun hideAndShowOtpView() {
        binding.apply {
            proceedAuthBtn.apply {
                setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.color_prim_one_40)
                )
                isEnabled = false
                text = getString(R.string.verify)
            }
            enterPhoneNumMain.hideView()
            callOtpNextFocus()
            fillOtpParent.show()
        }
        diffColorText(
            getString(R.string.please_fill_the_otp),
            "+91${binding.etPhoenNum.text}. <u>${getString(R.string.change_q)}</u>",
            binding.pleaseOtpTv
        )
        (activity as MainActivity).commonViewModel.timerStart(30000)
        (activity as MainActivity).commonViewModel.countNum.observe(viewLifecycleOwner, Observer {
            it?.let {
                var count = "$it"
                if (it < 10) {
                    count = "0$it"
                }
                Log.d("TAG", "hideAndShowOtpView: $it") //Didn’t receive? 00:30
                diffColorText("Didn’t receive?", "00:$count", binding.didnotReceiveTv)
            }
        })
    }

    private fun callOtpNextFocus() {
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
                R.id.otpOneEt -> {
                    if (text.length == 1) nextView?.requestFocus()

                }
                R.id.otpTwoEt -> {
                    if (text.length == 1) nextView?.requestFocus()
                }
                R.id.otpThreeEt -> {
                    if (text.length == 1) nextView?.requestFocus()
                }
                R.id.otpFourEt -> {
                    if (text.length == 1) {
                        nextView?.requestFocus()
                        binding.proceedAuthBtn.apply {
                            isEnabled = true
                            setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.splash_screen_one
                                )
                            )
                        }
                    } else {
                        binding.proceedAuthBtn.apply {
                            setBackgroundColor(
                                ContextCompat.getColor(requireContext(), R.color.color_prim_one_40)
                            )
                            isEnabled = false
                        }
                    }

                }
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
