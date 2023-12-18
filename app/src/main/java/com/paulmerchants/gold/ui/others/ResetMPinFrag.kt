package com.paulmerchants.gold.ui.others

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.ViewUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.CreditScoreScreenBinding
import com.paulmerchants.gold.databinding.ResetCardPinBinding
import com.paulmerchants.gold.databinding.ResetMPinBinding
import com.paulmerchants.gold.model.newmodel.ReqResetPin
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.CUST_MOBILE
import com.paulmerchants.gold.utility.disableButton
import com.paulmerchants.gold.utility.enableButton
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.showResetPinSuccessDialog
import com.paulmerchants.gold.viewmodels.ResetMpinViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResetMPinFrag : BaseFragment<ResetMPinBinding>(ResetMPinBinding::inflate) {

    private val resetMpinViewModel: ResetMpinViewModel by viewModels()

    override fun ResetMPinBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()
        focusAllEt()
        setActionCust()

        resetMpinViewModel.responseResetPin.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isSuccessful) {
                    if (it.body()?.status == "SUCCESS") {
                        "${it.body()?.message}".showSnackBar()
                        findNavController().navigateUp()
                    } else {
                        "${it.body()?.message}".showSnackBar()
                    }
                }
            }
        }
    }


    /**
     * validate all field for empty...
     */
    private fun validateAllField(): Boolean {
        return binding.pinCurrOneEt.text.isNotEmpty() &&
                binding.pinCurrTwoEt.text.isNotEmpty() &&
                binding.pinCurrThreeEt.text.isNotEmpty() &&
                binding.pinCurrFourEt.text.isNotEmpty() &&
                binding.pinOneNewEt.text.isNotEmpty() &&
                binding.pinTwoNewEt.text.isNotEmpty() &&
                binding.pinThreeNewEt.text.isNotEmpty() &&
                binding.pinFourNewEt.text.isNotEmpty() &&
                binding.pinOneCnfEt.text.isNotEmpty() &&
                binding.pinTwoCnfEt.text.isNotEmpty() &&
                binding.pinThreeCnfEt.text.isNotEmpty() &&
                binding.pinFourCnfEt.text.isNotEmpty()
    }

    private fun setActionCust() {
        binding.proceedAuthBtn.setOnClickListener {
            if (validateAllField()) {
                if ("${binding.pinOneNewEt.text}${binding.pinTwoNewEt.text}${binding.pinThreeNewEt.text}${binding.pinFourNewEt.text}" ==
                    "${binding.pinOneCnfEt.text}${binding.pinTwoCnfEt.text}${binding.pinThreeCnfEt.text}${binding.pinFourCnfEt.text}"
                ) {
                    resetMpinViewModel.resetMpin(
                        (activity as MainActivity).appSharedPref,
                        ReqResetPin(
                            "${binding.pinOneCnfEt.text}${binding.pinTwoCnfEt.text}${binding.pinThreeCnfEt.text}${binding.pinFourCnfEt.text}",
                            "${binding.pinCurrOneEt.text}${binding.pinCurrTwoEt.text}${binding.pinCurrThreeEt.text}${binding.pinCurrFourEt.text}",
                            (activity as MainActivity).appSharedPref?.getStringValue(CUST_MOBILE)
                                .toString(),  //static for testing
                            "${binding.pinOneNewEt.text}${binding.pinTwoNewEt.text}${binding.pinThreeNewEt.text}${binding.pinFourNewEt.text}",
                            AppUtility.getDeviceDetails()
                        )
                    )
                } else {
                    "New M-Pin and Confirm M-Pin mismatched".showSnackBar()
                }
            } else {
                "Please fill all fields".showSnackBar()
            }
        }
    }

    private fun focusAllEt() {
        currentMpinFocus()
        newMpinNextFocus()
        confirmMpinConfirmNextFocus()
    }

    private fun modifyHeaders() {

    }

    private fun currentMpinFocus() {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        binding.pinCurrOneEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinCurrOneEt, binding.pinCurrTwoEt
            )
        )
        binding.pinCurrTwoEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinCurrTwoEt, binding.pinCurrThreeEt
            )
        )
        binding.pinCurrThreeEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinCurrThreeEt, binding.pinCurrFourEt
            )
        )
        binding.pinCurrFourEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinCurrFourEt,
                null
            )
        )

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        binding.pinCurrOneEt.setOnKeyListener(GenericKeyEvent(binding.pinCurrOneEt, null))
        binding.pinCurrTwoEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinCurrTwoEt,
                binding.pinCurrOneEt
            )
        )
        binding.pinCurrThreeEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinCurrThreeEt,
                binding.pinCurrTwoEt
            )
        )
        binding.pinCurrFourEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinCurrFourEt,
                binding.pinCurrThreeEt
            )
        )
    }

    private fun newMpinNextFocus() {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        binding.pinOneNewEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinOneNewEt, binding.pinTwoNewEt
            )
        )
        binding.pinTwoNewEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinTwoNewEt, binding.pinThreeNewEt
            )
        )
        binding.pinThreeNewEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinThreeNewEt, binding.pinFourNewEt
            )
        )
        binding.pinFourNewEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinFourNewEt,
                null
            )
        )

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        binding.pinOneNewEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinOneNewEt,
                null
            )
        )
        binding.pinTwoNewEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinTwoNewEt,
                binding.pinOneNewEt
            )
        )
        binding.pinThreeNewEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinThreeNewEt,
                binding.pinTwoNewEt
            )
        )
        binding.pinFourNewEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinFourNewEt,
                binding.pinThreeNewEt
            )
        )
    }

    private fun confirmMpinConfirmNextFocus() {
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        binding.pinOneCnfEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinOneCnfEt, binding.pinTwoCnfEt
            )
        )
        binding.pinTwoCnfEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinTwoCnfEt,
                binding.pinThreeCnfEt
            )
        )
        binding.pinThreeCnfEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinThreeCnfEt,
                binding.pinFourCnfEt
            )
        )
        binding.pinFourNewEt.addTextChangedListener(
            GenericTextWatcher(
                binding.pinFourCnfEt,
                null
            )
        )

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        binding.pinOneCnfEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinOneCnfEt,
                null
            )
        )
        binding.pinTwoCnfEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinTwoCnfEt,
                binding.pinOneCnfEt
            )
        )
        binding.pinThreeCnfEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinThreeCnfEt,
                binding.pinTwoCnfEt
            )
        )
        binding.pinFourCnfEt.setOnKeyListener(
            GenericKeyEvent(
                binding.pinFourCnfEt,
                binding.pinThreeCnfEt
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
                R.id.pinCurrOneEt -> {
                    if (text.length == 1) {
                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.proceedAuthBtn.disableButton(requireContext())
                    }
                }

                R.id.pinCurrTwoEt -> {
                    if (text.length == 1) {
                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.proceedAuthBtn.disableButton(requireContext())
                    }
                }

                R.id.pinCurrThreeEt -> {
                    if (text.length == 1) {
                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.proceedAuthBtn.disableButton(requireContext())
                    }
                }

                R.id.pinCurrFourEt -> {
                    if (text.length == 1) {
                        binding.proceedAuthBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.proceedAuthBtn.disableButton(requireContext())
                    }
                }

                R.id.pinOneNewEt -> {
                    if (text.length == 1) {
//                                    binding.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                                    binding.signUpBtn.disableButton(requireContext())
                    }
                }

                R.id.pinTwoNewEt -> {
                    if (text.length == 1) {
//                                    binding.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                                    binding.signUpBtn.disableButton(requireContext())
                    }
                }

                R.id.pinThreeNewEt -> {
                    if (text.length == 1) {
//                                    binding.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                                    binding.signUpBtn.disableButton(requireContext())
                    }
                }

                R.id.pinFourNewEt -> {
                    if (text.length == 1) {
//                                    binding.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                                    binding.signUpBtn.disableButton(requireContext())
                    }

                }

                R.id.pinOneCnfEt -> {
                    if (text.length == 1) {
//                                    binding.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                                    binding.signUpBtn.disableButton(requireContext())
                    }
                }

                R.id.pinTwoCnfEt -> {
                    if (text.length == 1) {
//                                    binding.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                                    binding.signUpBtn.disableButton(requireContext())
                    }
                }

                R.id.pinThreeCnfEt -> {
                    if (text.length == 1) {
//                                    binding.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                                    binding.signUpBtn.disableButton(requireContext())
                    }
                }

                R.id.pinFourCnfEt -> {
                    if (text.length == 1) {
//                                    binding.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
//                                    binding.signUpBtn.disableButton(requireContext())
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