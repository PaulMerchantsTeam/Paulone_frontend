package com.paulmerchants.gold.ui.auth

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.PhoneAuthFragmentBinding
import com.paulmerchants.gold.utility.*
import com.paulmerchants.gold.utility.AppUtility.changeStatusBarWithReqdColor
import com.paulmerchants.gold.utility.AppUtility.diffColorText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PhoenNumVerifiactionFragment :
    BaseFragment<PhoneAuthFragmentBinding>(PhoneAuthFragmentBinding::inflate) {
    private var isMobileEntered: Boolean = false
    private var isOtpVerified: Boolean = false

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
            "",
            "",
            "",
            binding.titleWelcomTv
        )
        binding.proceedAuthBtn.setOnClickListener {
            if (!isMobileEntered) {
                if (binding.etPhoenNum.text.isNotEmpty()) {
                    hideAndShowOtpView()
                    isMobileEntered = true
                }
            } else {
                if (binding.otpOneEt.text.isNotEmpty() && binding.otpTwoEt.text.isNotEmpty() && binding.otpThreeEt.text.isNotEmpty() && binding.otpFourEt.text.isNotEmpty()) {
                    hideAndShowProgressView(false)
                    lifecycleScope.launch {
                        delay(2000)
                        binding.mainPgCons.cirStreakTimePg.endProgress(requireContext())
                        binding.mainPgCons.progessTv.apply {
                            setTColor(
                                getString(R.string.verified),
                                requireContext(), R.color.green_verified
                            )
                        }
                        delay(1000)
                    }
                    isOtpVerified = true
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
                            requireContext(), R.color.splash_screen_one
                        )
                    )
                } else {
                    binding.proceedAuthBtn.isEnabled = false
                    binding.proceedAuthBtn.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.color_prim_one_40
                        )
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun hideAndShowSignUpScreen() {
        (activity as MainActivity).mViewModel.timer?.cancel()
        binding.fillOtpParent.hideView()
        binding.signUpParentMain.root.show()
        customizeText()
        callMpinNextFocus()


        binding.signUpParentMain.signUpBtn.setOnClickListener {
            if (binding.signUpParentMain.etName.text.isNotEmpty() &&
                binding.signUpParentMain.etEmailId.text.isNotEmpty() && binding.signUpParentMain.mpinOneEt.text.isNotEmpty() &&
                binding.signUpParentMain.mpinTwoEt.text.isNotEmpty() && binding.signUpParentMain.mpinThreeEt.text.isNotEmpty()
                && binding.signUpParentMain.mpinFourEt.text.isNotEmpty() && binding.signUpParentMain.termsCb.isChecked
            ) {
                binding.signUpParentMain.root.hideView()
                hideAndShowProgressView(true)
            }
        }
        binding.signUpParentMain.googleSignInTv.setOnClickListener {
            //flow_remaining...
        }

    }

    private fun customizeText() {
        diffColorText(
            "Please",
            "create",
            "an account before we proceed.",
            "",
            "",
            "",
            binding.signUpParentMain.createAccTitleTv
        )
        diffColorText(
            getString(R.string.already_have_an_acc),
            getString(R.string.log_in),
            binding.signUpParentMain.alreadyHaveAccTv
        )
    }

    private fun hideAndShowProgressView(isSignedUp: Boolean) {
        if (!isSignedUp) {
            diffColorText(
                "Give us some moment to",
                "verify",
                "few things. Much appreciated",
                "",
                "",
                "",
                binding.titleWelcomTv
            )
            binding.fillOtpParent.hideView()
        } else {
            diffColorText(
                getString(R.string.this_may_take_some),
                getString(R.string.us),
                binding.titleWelcomTv
            )
            lifecycleScope.launch {
                binding.mainPgCons.progessTv.apply {
                    setTColor(getString(R.string.verifying), requireContext(), R.color.yellow_main)
                }
                binding.mainPgCons.cirStreakTimePg.startProgress(requireContext())
                delay(1000)
                binding.mainPgCons.progessTv.apply {
                    setTColor(
                        getString(R.string.verified),
                        requireContext(),
                        R.color.green_verified
                    )
                }
                binding.mainPgCons.cirStreakTimePg.endProgress(requireContext())

                delay(1000)
                hideAndShowSignUpDoneScreen()
            }
        }
        binding.apply {
            mainPgCons.root.show()
            proceedAuthBtn.hideView()
        }

    }

    private fun hideAndShowSignUpDoneScreen() {
        binding.titleWelcomTv.hideView()
        binding.mainPgCons.root.hideView()
        diffColorText(
            "And we are",
            "finally",
            "done, welcome",
            " home ",
            " user . We are glad to have ", "you with us ",
            binding.createMpinAndSuccessMain.finallySignUpDoneTv
        )

        binding.createMpinAndSuccessMain.root.show()
        lifecycleScope.launchWhenCreated {
            delay(2000)
            findNavController().navigate(R.id.homeScreenFrag)
        }
    }

    private fun hideAndShowOtpView() {
        binding.apply {
            proceedAuthBtn.apply {
                disableButton(requireContext())
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
        (activity as MainActivity).mViewModel.timerStart(30000)
        (activity as MainActivity).mViewModel.countNum.observe(viewLifecycleOwner, Observer {
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
                binding.otpOneEt, binding.otpTwoEt
            )
        )
        binding.otpTwoEt.addTextChangedListener(
            GenericTextWatcher(
                binding.otpTwoEt, binding.otpThreeEt
            )
        )
        binding.otpThreeEt.addTextChangedListener(
            GenericTextWatcher(
                binding.otpThreeEt, binding.otpFourEt
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


    private fun callMpinNextFocus() {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        binding.signUpParentMain.mpinOneEt.addTextChangedListener(
            GenericTextWatcher(
                binding.signUpParentMain.mpinOneEt, binding.signUpParentMain.mpinTwoEt
            )
        )
        binding.signUpParentMain.mpinTwoEt.addTextChangedListener(
            GenericTextWatcher(
                binding.signUpParentMain.mpinTwoEt, binding.signUpParentMain.mpinThreeEt
            )
        )
        binding.signUpParentMain.mpinThreeEt.addTextChangedListener(
            GenericTextWatcher(
                binding.signUpParentMain.mpinThreeEt, binding.signUpParentMain.mpinFourEt
            )
        )
        binding.signUpParentMain.mpinFourEt.addTextChangedListener(
            GenericTextWatcher(
                binding.signUpParentMain.mpinFourEt,
                null
            )
        )

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        binding.signUpParentMain.mpinOneEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinOneEt,
                null
            )
        )
        binding.signUpParentMain.mpinTwoEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinTwoEt,
                binding.signUpParentMain.mpinOneEt
            )
        )
        binding.signUpParentMain.mpinThreeEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinThreeEt,
                binding.signUpParentMain.mpinTwoEt
            )
        )
        binding.signUpParentMain.mpinFourEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinFourEt,
                binding.signUpParentMain.mpinThreeEt
            )
        )
    }


    inner class GenericKeyEvent internal constructor(
        private val currentView: EditText, private val previousView: EditText?
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
        private val currentView: View, private val nextView: View?
    ) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (currentView.id) {
                R.id.otpOneEt -> {
                    if (text.length == 1) {
                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.proceedAuthBtn.disableButton(requireContext())
                    }

                }
                R.id.otpTwoEt -> {
                    if (text.length == 1) {
                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.proceedAuthBtn.disableButton(requireContext())
                    }
                }
                R.id.otpThreeEt -> {
                    if (text.length == 1) {
                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.proceedAuthBtn.disableButton(requireContext())
                    }
                }
                R.id.otpFourEt -> {
                    if (text.length == 1) {
                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.proceedAuthBtn.disableButton(requireContext())
                    }
                }

                R.id.mpinOneEt -> {
                    if (text.length == 1) {
                        binding.signUpParentMain.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.signUpParentMain.signUpBtn.disableButton(requireContext())
                    }
                }
                R.id.mpinTwoEt -> {
                    if (text.length == 1) {
                        binding.signUpParentMain.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.signUpParentMain.signUpBtn.disableButton(requireContext())
                    }
                }
                R.id.mpinThreeEt -> {
                    if (text.length == 1) {
                        binding.signUpParentMain.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.signUpParentMain.signUpBtn.disableButton(requireContext())
                    }
                }
                R.id.mpinFourEt -> {
                    if (text.length == 1) {
                        binding.signUpParentMain.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.signUpParentMain.signUpBtn.disableButton(requireContext())
                    }

                }
                //You can use EditText4 same as above to hide the keyboard
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int
        ) {
        }

        override fun onTextChanged(
            arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int
        ) {
        }

    }

}
