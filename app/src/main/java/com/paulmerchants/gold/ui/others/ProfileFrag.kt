package com.paulmerchants.gold.ui.others

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.OtpFillLayoutDialogBinding
import com.paulmerchants.gold.databinding.ProfileLayoutBinding
import com.paulmerchants.gold.enums.ScreenType
import com.paulmerchants.gold.model.MenuServices
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.CUST_MOBILE
import com.paulmerchants.gold.utility.Constants.IS_RESET_MPIN
import com.paulmerchants.gold.utility.disableButton
import com.paulmerchants.gold.utility.enableButton
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.setServicesUi
import com.paulmerchants.gold.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *
{
"DisplayName": "SEEMA",
"Photo":",
"MobileNo": "8968059147",
"AadhaarNo": "908957640028",
"PAN": "LHJPS7444D",
"Email": "",
"MailingAddress": "MSSEEMA,H.NO 42,MAULI JAGRAN FLAT PART-2CHANDIGARH ,Aerodrome Chandigarh [P.O],CHANDIGARH,160003"
}
 */

@AndroidEntryPoint
class ProfileFrag : BaseFragment<ProfileLayoutBinding>(ProfileLayoutBinding::inflate) {

    private val profileViewModel: ProfileViewModel by viewModels()
    private var customDialog: androidx.appcompat.app.AlertDialog? = null
    override fun ProfileLayoutBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()

        val backStack = findNavController().backQueue
        for (i in backStack) {
            Log.d(
                "TAG",
                "STACK__COUNT_NAME: ...${i.id}..--------.${i.destination.displayName}"
            )
        }
        if (profileViewModel.isCalled) {
            (activity as MainActivity).appSharedPref?.let { profileViewModel.getCustomerDetails(it) }
        }
        profileViewModel.getRespCustomersDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.nameUserTv.text = it.DisplayName ?: "NA"
                it.DisplayName?.let {
                    (activity as MainActivity).appSharedPref?.putStringValue(
                        Constants.CUSTOMER_NAME,
                        it
                    )
                }
                binding.emailUserIv.text = it.MailingAddress ?: "NA"
                binding.userNumTv.text = it.MobileNo ?: "NA"
//                Glide.with(requireContext()).load(it.Photo?.toByteArray()).into(binding.backIv)
            }
        }
        handlesClicks()
        settingUi()
        profileViewModel.verifyOtp.observe(viewLifecycleOwner) {
            it?.let {
                if (it.statusCode == "200") {
                    Log.e("TAG", "onStart: .=======${it.data}")
                    val bundle = Bundle().apply {
                        putBoolean(IS_RESET_MPIN, true)
                    }
                    findNavController().navigate(R.id.resetMPinFrag, bundle)
                } else {
                    Log.e("TAG", "onStart: .=======${it.data}")
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        profileViewModel.isCalled = false
    }

    private fun handlesClicks() {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.editProfileBtn.setOnClickListener {
            binding.toolbar.hide()
            binding.appBarCollaps.setBackgroundColor(resources.getColor(R.color.splash_screen_two))
            binding.backProfileIv.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(), R.anim.slide_zoom_out
                )
            )
            lifecycleScope.launch {
                delay(300)
                findNavController().navigate(
                    R.id.editProfileScreenFrag, null
                )
            }

        }
    }

    private fun settingUi() {
        binding.profileSettingsRv.setServicesUi(
            requireContext(),
            ::onMenuServiceClicked,
            ::onMenuServiceClickedTwo,
            ::onMenuServiceClickedTitle
        )
    }

    private fun onMenuServiceClicked(menuServices: MenuServices) {
        var headerValue: Int? = null
        when (menuServices.serviceId) {
            100 -> {
                headerValue = 100

                val changePinBundle = Bundle().apply {
                    putInt("ProfileChangePin", headerValue)
                }

                findNavController().navigate(R.id.resetMPinFrag)
            }

            101 -> {
                findNavController().navigate(R.id.transactionFrag)
            }

            103 -> {
                //Need Ui for your spent
            }

            104 -> {

                startActivity( Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")))


//playStore


            }

            105 -> {

            }

            else -> {

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

        dialogBinding.verifyOtpBtn.setOnClickListener {
            if (dialogBinding.otpOneEt.text.isNotEmpty() && dialogBinding.otpTwoEt.text.isNotEmpty() &&
                dialogBinding.otpThreeEt.text.isNotEmpty() && dialogBinding.otpFourEt.text.isNotEmpty()
            ) {
                profileViewModel.verifyOtp(
                    (activity as MainActivity).appSharedPref,
                    mobile,
                    otp = "${dialogBinding.otpOneEt.text}${dialogBinding.otpTwoEt.text}" +
                            "${dialogBinding.otpThreeEt.text}${dialogBinding.otpFourEt.text}"
                )
                customDialog?.dismiss()
            } else {
                "Please fill Otp".showSnackBar()
            }
            //verify Otp
        }
        dialogBinding.cancelDgBtn.setOnClickListener {
            customDialog?.dismiss()
            //cancel Otp
        }
    }

    private fun onMenuServiceClickedTwo(menuServices: MenuServices) {
        when (menuServices.serviceId) {
            100 -> {
                requireActivity().runOnUiThread {
                    showCustomDialogOTPVerify(
                        (activity as MainActivity).appSharedPref?.getStringValue(
                            CUST_MOBILE
                        ).toString(),
                        title = "OTP send to the number ${
                            (activity as MainActivity).appSharedPref?.getStringValue(
                                CUST_MOBILE
                            )
                        }"
                    )
                }
                (activity as MainActivity).appSharedPref?.getStringValue(
                    CUST_MOBILE
                )?.let { profileViewModel.getOtp((activity as MainActivity).appSharedPref, it) }
            }

            104 -> {
                findNavController().navigate(R.id.raiseComplaintFrag)
            }
        }
    }

    private fun onMenuServiceClickedTitle(menuServices: MenuServices) {
        when (menuServices.serviceId) {
            105 -> {
                findNavController().navigate(
                    R.id.logoutDialog
                )
            }
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

    inner class GenericKeyEvent internal constructor(
        private val currentView: EditText, private val previousView: EditText?,
    ) : View.OnKeyListener {
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.pinCurrOneEt && currentView.text.isEmpty()) {
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