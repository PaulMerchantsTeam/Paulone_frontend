package com.paulmerchants.gold.ui.auth

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.LoginWithMobileMpinBinding
import com.paulmerchants.gold.model.newmodel.ReqLoginWithMpin
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.Constants.CUST_MOBILE
import com.paulmerchants.gold.utility.disableButton
import com.paulmerchants.gold.utility.enableButton
import com.paulmerchants.gold.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginScreenFrag :
    BaseFragment<LoginWithMobileMpinBinding>(LoginWithMobileMpinBinding::inflate) {

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
            (activity as MainActivity).appSharedPref?.getStringValue(CUST_MOBILE)
                .toString()
        )
        binding.loginWithDifferentAccTv.setOnClickListener {
            Toast.makeText(requireContext(), "CLICKED", Toast.LENGTH_SHORT).show()
            (activity as MainActivity).appSharedPref?.putBoolean(Constants.OTP_VERIFIED, false)
//            findNavController().popBackStack(R.id.loginScreenFrag, true)
            findNavController().navigate(R.id.phoenNumVerifiactionFragment)
        }
        binding.signUpBtn.setOnClickListener {
            if (isValidate()) {
                loginViewModel.loginWithMpin(
                    findNavController(),
                    (activity as MainActivity).appSharedPref,
                    ReqLoginWithMpin(
                        binding.etPhoenNum.text.toString(),
                        "${binding.pinOneEt.text}${binding.pinTwoEt.text}${binding.pinThreeEt.text}${binding.pinFourEt.text}"
                    )
                )
            }
        }

        loginViewModel.getTokenResp.observe(viewLifecycleOwner) {
            it?.let {
                if (it.code() == 200) {
                    if (isValidate()) {
                        loginViewModel.loginWithMpin(
                            findNavController(),
                            (activity as MainActivity).appSharedPref,
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


}