package com.paulmerchants.gold.ui.auth

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.PhoneAuthFragmentBinding
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility.changeStatusBarWithReqdColor
import com.paulmerchants.gold.utility.AppUtility.diffColorText
import com.paulmerchants.gold.utility.AppUtility.noInternetDialog
import com.paulmerchants.gold.utility.AppUtility.openUrl
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.IS_LOGOUT
import com.paulmerchants.gold.utility.Constants.IS_USER_EXIST
import com.paulmerchants.gold.utility.Constants.OTP_VERIFIED
import com.paulmerchants.gold.utility.Constants.SIGNUP_DONE
import com.paulmerchants.gold.utility.InternetUtils
import com.paulmerchants.gold.utility.disableButton
import com.paulmerchants.gold.utility.enableButton
import com.paulmerchants.gold.utility.endProgress
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.hideView
import com.paulmerchants.gold.utility.setTColor
import com.paulmerchants.gold.utility.shoViewWithAnim
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.utility.startProgress
import com.paulmerchants.gold.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PhoneNumVerificationFragment :
    BaseFragment<PhoneAuthFragmentBinding>(PhoneAuthFragmentBinding::inflate) {

    private var isResendEnabled: Boolean = false
    private var isMobileEntered: Boolean = false
    private var isOtpVerified: Boolean = false
    private var pinValue: Int? = null
    private val authViewModel: AuthViewModel by viewModels()

    override fun PhoneAuthFragmentBinding.initialize() {
        changeStatusBarWithReqdColor(requireActivity(), R.color.pg_color)
        pinValue = arguments?.getInt("ProfileChangePin", 0)
        Log.d("TAG", "initialize: ........${arguments?.getBoolean(IS_LOGOUT, false)}")
        authViewModel.isFrmLogout = arguments?.getBoolean(IS_LOGOUT, false)
    }

    private fun isValidate(): Boolean {
        val confirmPin =
            (binding.signUpParentMain.mpinOneConfirmEt.text.toString() + binding.signUpParentMain.mpinConfirmTwoEt.text.toString() + binding.signUpParentMain.mpinConfirmThreeEt.text.toString() + binding.signUpParentMain.mpinConfirmFourEt.text.toString())
        val mPin =
            (binding.signUpParentMain.mpinOneEt.text.toString() + binding.signUpParentMain.mpinTwoEt.text.toString() + binding.signUpParentMain.mpinThreeEt.text.toString() + binding.signUpParentMain.mpinFourEt.text.toString())
        return when {
            confirmPin != mPin -> {
                "M-Pin Mismatched ".showSnackBar()
                false
            }

            binding.signUpParentMain.mpinOneEt.text.isEmpty() || binding.signUpParentMain.mpinTwoEt.text.isEmpty() || binding.signUpParentMain.mpinThreeEt.text.isEmpty() || binding.signUpParentMain.mpinFourEt.text.isEmpty() -> {
                "Please enter M-Pin".showSnackBar()
                false
            }

            binding.signUpParentMain.mpinOneConfirmEt.text.isEmpty() || binding.signUpParentMain.mpinConfirmTwoEt.text.isEmpty() || binding.signUpParentMain.mpinConfirmThreeEt.text.isEmpty() || binding.signUpParentMain.mpinConfirmFourEt.text.isEmpty()

            -> {
                "Please enter Confirm M-Pin".showSnackBar()
                false
            }

            !binding.signUpParentMain.termsCb.isChecked -> {
                "Please accept Terms and Conditions".showSnackBar()
                false
            }

            else -> true
        }
    }


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).checkForDownFromRemoteConfig()
        (activity as MainActivity).commonViewModel.isRemoteConfigCheck.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    (activity as MainActivity).showUnderMainTainPage()
                }
            }
        }


        //Welcome to Paul Gold,
        //we are happy to serve you!!
        callMpinNextFocus()
        callMpinConfirmNextFocus()
        AppSharedPref.getStringValue(Constants.CUSTOMER_NAME)?.let {
            if (it != "") {
                binding.signUpParentMain.etName.apply {
                    setText(it)
                    isEnabled = false
                }
            }
        }

        binding.signUpParentMain.privacy.setOnClickListener {
            openUrl(requireContext(), BuildConfig.PRIVACY_POLICY)
        }
        binding.signUpParentMain.tAndCTv.setOnClickListener {
            openUrl(requireContext(), BuildConfig.TERMS_CONDITION)
        }
        binding.signUpParentMain.signUpBtn.setOnClickListener {
            if (InternetUtils.isNetworkAvailable(requireContext())) {
                binding.enterPhoneNumMain.hide()

                if (binding.signUpParentMain.etEmailId.text.isNotEmpty()) {


                    if (isValidate()) {
                        val confirmPin =
                            (binding.signUpParentMain.mpinOneConfirmEt.text.toString() + binding.signUpParentMain.mpinConfirmTwoEt.text.toString() + binding.signUpParentMain.mpinConfirmThreeEt.text.toString() + binding.signUpParentMain.mpinConfirmFourEt.text.toString())
                        val mPin =
                            (binding.signUpParentMain.mpinOneEt.text.toString() + binding.signUpParentMain.mpinTwoEt.text.toString() + binding.signUpParentMain.mpinThreeEt.text.toString() + binding.signUpParentMain.mpinFourEt.text.toString())
                        authViewModel.setMpin(
                            confirmMPin = confirmPin,
                            setUpMPin = mPin,
                            email = binding.signUpParentMain.etEmailId.text.toString(),
                            (activity as MainActivity).mLocation, requireContext()
                        )
                    }
                } else {
                    "Please enter Email".showSnackBar()
                }
            } else {
                noInternetDialog()
            }

        }

        if (pinValue == 100) {
            binding.apply {
                titleWelcomTvAuth.hide()
                pleaseTv.hide()
                etTv.hide()
                etPhoenNum.hide()
                proceedAuthBtn.hide()
                etPhoenNum.hide()
                createMpinAndSuccessMain.root.show()
                createMpinAndSuccessMain.setUpMPinParent.show()
                createMpinAndSuccessMain.finalSIgnUpParent.hide()
                hideAndShowOtpView()
                callMpinNextFocus()
                callMpinConfirmNextFocus()
            }
        } else {
            diffColorText(
                "Welcome to Paul One,\nwe are",
                "happy",
                "to serve you!!",
                "",
                "",
                "",
                binding.titleWelcomTvAuth
            )
        }
        authViewModel.isCustomerExist.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    authViewModel.timerStart()
                    hideAndShowOtpView()
                    isMobileEntered = true
                } else {
                    //hide..process start again
                }
            }
        }
        authViewModel.MPinLivedata.observe(viewLifecycleOwner) {
            it?.let {
                if (it.status_code == 200) {


                    binding.signUpParentMain.root.hideView()
                    hideAndShowProgressView(true)


                } else if (it.status_code == 498) {
                    findNavController().popBackStack(R.id.phoenNumVerifiactionFragment, true)
                    findNavController().navigate(R.id.phoenNumVerifiactionFragment)
                }
            }
        }

        authViewModel.verifyOtp.observe(viewLifecycleOwner) {
            it?.let {
                if (it.status_code == 200) {
                    if (it.data?.user_exist == false) {
                        hideAndShowSignUpScreen()
                    } else {
                        AppSharedPref.putBoolean(IS_USER_EXIST, true)
                        findNavController().popBackStack(R.id.phoenNumVerifiactionFragment, true)
                        findNavController().navigate(R.id.loginScreenFrag)
                    }
                } else if (it.status_code == 498) {
                    findNavController().popBackStack(R.id.phoenNumVerifiactionFragment, true)
                    findNavController().navigate(R.id.phoenNumVerifiactionFragment)
//                    hideAndShowNumInputView()
                } else {
                    //
                }

            }
        }
        authViewModel.isOtpVerify.observe(viewLifecycleOwner) {
            it?.let {
                hideAndShowProgressView(false)
                lifecycleScope.launch {
                    delay(2000)
                    binding.mainPgCons.cirStreakTimePg.endProgress(requireContext())
                    binding.mainPgCons.progessTv.apply {
                        setTColor(
                            getString(R.string.verified),
                            requireContext(),
                            R.color.green_verified
                        )
                    }
                    delay(1000)
                }
                isOtpVerified = true
                AppSharedPref.putBoolean(OTP_VERIFIED, isOtpVerified)
            }
        }

        binding.proceedAuthBtn.setOnClickListener {
            if (InternetUtils.isNetworkAvailable(requireContext())) {
                if (!isMobileEntered) {
                    if (binding.etPhoenNum.text.isNotEmpty()) {



                        authViewModel.getOtp(binding.etPhoenNum.text.toString(), requireActivity())

                    }
                } else {

                    if (binding.otpOneEt.text.isNotEmpty() && binding.otpTwoEt.text.isNotEmpty() && binding.otpThreeEt.text.isNotEmpty() && binding.otpFourEt.text.isNotEmpty()) {
                        val otp =
                            binding.otpOneEt.text.toString() + binding.otpTwoEt.text.toString() + binding.otpThreeEt.text.toString() + binding.otpFourEt.text.toString()




                        if (otp.isNotEmpty()) {
                            authViewModel.verifyOtp(
                                binding.etPhoenNum.text.toString(),
                                otp,
                                (activity as MainActivity).mLocation, requireContext()
                            )

                        }

//
                    }
                }
            } else {
                noInternetDialog()
            }
        }
        binding.pleaseOtpTv.setOnClickListener {
            //change Num
            //show Num Input Ui, hide otp layout
            authViewModel.isCustomerExist.postValue(false)
            authViewModel.timer?.cancel()
            authViewModel.isCalledApi = true
            isMobileEntered = false
            hideAndShowNumInputView()
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

        //Send Otp Again...
        binding.didnotReceiveTv.setOnClickListener {
            if (!isResendEnabled) {
                return@setOnClickListener
            } else {
                if (binding.etPhoenNum.text.toString().isNotEmpty()) {
                    authViewModel.getOtp(
                        binding.etPhoenNum.text.toString(), requireActivity()
                    )
                    authViewModel.timerStart()
                }
            }
        }
    }

    private fun hideAndShowNumInputView() {
        (activity as MainActivity).mViewModel.timer?.cancel()
        (activity as MainActivity).mViewModel.countNum.postValue(0L)
        binding.fillOtpParent.hide()
        binding.enterPhoneNumMain.shoViewWithAnim()
        binding.apply {
            proceedAuthBtn.apply {
                disableButton(requireContext())
                text = getString(R.string.prceed)
            }
        }
    }


    private fun hideAndShowSignUpScreen() {
        authViewModel.timer?.cancel()
        authViewModel.countNum.postValue(0L)
        binding.fillOtpParent.hideView()
        binding.signUpParentMain.root.show()
        customizeText()
        callMpinNextFocus()
        callMpinConfirmNextFocus()
        AppSharedPref.getStringValue(Constants.CUSTOMER_NAME)?.let {
            if (it != "") {
                binding.signUpParentMain.etName.apply {
                    setText(it)
                    isEnabled = false
                }
            }
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
                binding.titleWelcomTvAuth
            )
            binding.fillOtpParent.hideView()
        } else {
            diffColorText(
                getString(R.string.this_may_take_some),
                getString(R.string.us),
                binding.titleWelcomTvAuth
            )
            lifecycleScope.launch {
                binding.mainPgCons.progessTv.apply {
                    setTColor(getString(R.string.verifying), requireContext(), R.color.yellow_main)
                }
                binding.mainPgCons.cirStreakTimePg.startProgress()
                delay(1000)
                binding.mainPgCons.progessTv.apply {
                    setTColor(
                        getString(R.string.verified), requireContext(), R.color.green_verified
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
        binding.titleWelcomTvAuth.hideView()
        binding.mainPgCons.root.hideView()
        diffColorText(
            "And we are",
            "finally",
            "done, welcome",
            " home ",
            " user . We are glad to have ",
            "you with us ",
            binding.createMpinAndSuccessMain.finallySignUpDoneTv
        )

        binding.createMpinAndSuccessMain.root.show()
        AppSharedPref.putBoolean(SIGNUP_DONE, true)
        lifecycleScope.launchWhenCreated {
            delay(2000)
            findNavController().popBackStack(R.id.phoenNumVerifiactionFragment, true)
            findNavController().navigate(
                R.id.loginScreenFrag
            )
        }
    }

    //Handle--Configuration...
    override fun onPause() {
        super.onPause()
        authViewModel.isCalledApi = false
    }

    private fun hideAndShowOtpView() {
        authViewModel.enteredMobileTemp = binding.etPhoenNum.text.toString()
        Log.d("TAG", "hideAndShowOtpView: ..............${authViewModel.enteredMobileTemp}")
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

        if (authViewModel.isCalledApi) {
            authViewModel.timerStart()
        }
        authViewModel.countStr.observe(viewLifecycleOwner) {



            if (it == "00") {
                isResendEnabled = true
                binding.didnotReceiveTv.setTColor(
                    getString(R.string.send_again), requireContext(), R.color.splash_screen_one
                )
            } else {
                diffColorText("Didn’t receive?", it, binding.didnotReceiveTv)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authViewModel.timer?.cancel()
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
                binding.signUpParentMain.mpinFourEt, null
            )
        )

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        binding.signUpParentMain.mpinOneEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinOneEt, null
            )
        )
        binding.signUpParentMain.mpinTwoEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinTwoEt, binding.signUpParentMain.mpinOneEt
            )
        )
        binding.signUpParentMain.mpinThreeEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinThreeEt, binding.signUpParentMain.mpinTwoEt
            )
        )
        binding.signUpParentMain.mpinFourEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinFourEt, binding.signUpParentMain.mpinThreeEt
            )
        )
    }


    private fun callMpinConfirmNextFocus() {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        binding.signUpParentMain.mpinOneConfirmEt.addTextChangedListener(
            GenericTextWatcher(
                binding.signUpParentMain.mpinOneConfirmEt, binding.signUpParentMain.mpinConfirmTwoEt
            )
        )
        binding.signUpParentMain.mpinConfirmTwoEt.addTextChangedListener(
            GenericTextWatcher(
                binding.signUpParentMain.mpinConfirmTwoEt,
                binding.signUpParentMain.mpinConfirmThreeEt
            )
        )
        binding.signUpParentMain.mpinConfirmThreeEt.addTextChangedListener(
            GenericTextWatcher(
                binding.signUpParentMain.mpinConfirmThreeEt,
                binding.signUpParentMain.mpinConfirmFourEt
            )
        )
        binding.signUpParentMain.mpinFourEt.addTextChangedListener(
            GenericTextWatcher(
                binding.signUpParentMain.mpinConfirmFourEt, null
            )
        )

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        binding.signUpParentMain.mpinOneConfirmEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinOneConfirmEt, null
            )
        )
        binding.signUpParentMain.mpinConfirmTwoEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinConfirmTwoEt, binding.signUpParentMain.mpinOneConfirmEt
            )
        )
        binding.signUpParentMain.mpinConfirmThreeEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinConfirmThreeEt,
                binding.signUpParentMain.mpinConfirmTwoEt
            )
        )
        binding.signUpParentMain.mpinConfirmFourEt.setOnKeyListener(
            GenericKeyEvent(
                binding.signUpParentMain.mpinConfirmFourEt,
                binding.signUpParentMain.mpinConfirmThreeEt
            )
        )
    }


    inner class GenericKeyEvent internal constructor(
        private val currentView: EditText, private val previousView: EditText?,
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
        private val currentView: View, private val nextView: View?,
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

                R.id.mpinOneConfirmEt -> {
                    if (text.length == 1) {
                        binding.signUpParentMain.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.signUpParentMain.signUpBtn.disableButton(requireContext())
                    }
                }

                R.id.mpinConfirmTwoEt -> {
                    if (text.length == 1) {
                        binding.signUpParentMain.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.signUpParentMain.signUpBtn.disableButton(requireContext())
                    }
                }

                R.id.mpinConfirmThreeEt -> {
                    if (text.length == 1) {
                        binding.signUpParentMain.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.signUpParentMain.signUpBtn.disableButton(requireContext())
                    }
                }

                R.id.mpinConfirmFourEt -> {
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
            arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int,
        ) {
        }

        override fun onTextChanged(
            arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int,
        ) {
        }

    }

}
