package com.paulmerchants.gold.ui.btmsheetDg

//import com.razorpay.Checkout
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.ResetMPinForgetBinding
import com.paulmerchants.gold.model.newmodel.ReqResetForgetPin
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.disableButton
import com.paulmerchants.gold.utility.enableButton
import com.paulmerchants.gold.viewmodels.ResetMpinViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * use onCreateDialog for code touching the dialog created by onCreateDialog,
 * onViewCreated(View, Bundle) for code touching the view created by
 * onCreateView and onCreate(Bundle) for other initialization.
 *
 * To get a callback specifically when a Fragment activity's
 * Activity.onCreate(Bundle) is called, register a androidx.lifecycle.
 *
 * LifecycleObserver on the Activity's Lifecycle in onAttach(Context),
 * removing it when it receives the Lifecycle.State.CREATED callback.
 * Params:savedInstanceState – If the fragment is being re-created from a previous saved state, this is the state.
 *
 */

@AndroidEntryPoint
class ResetMpinDialog : BottomSheetDialogFragment() {

    lateinit var binding: ResetMPinForgetBinding
    val TAG = "ResetMpinDialog"
    private val resetMpinViewModel: ResetMpinViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = ResetMPinForgetBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ")
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusAllEt()
        setActionCust()
        binding.cancelDgBtn.setOnClickListener {
            dismiss()
        }
        resetMpinViewModel.responseResetForgetPin.observe(viewLifecycleOwner) {
            it?.let {
                if (it.body()?.statusCode == "200") {
                    "${it.body()?.message}".showSnackBar()
                    dismiss()
                } else {
                    "${it.body()?.message}".showSnackBar()
                }
            }
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)

    }

    private fun setActionCust() {
        binding.proceedAuthBtn.setOnClickListener {
            if (validateAllFieldForReset()) {
                if ("${binding.pinOneNewEt.text}${binding.pinTwoNewEt.text}${binding.pinThreeNewEt.text}${binding.pinFourNewEt.text}" ==
                    "${binding.pinOneCnfEt.text}${binding.pinTwoCnfEt.text}${binding.pinThreeCnfEt.text}${binding.pinFourCnfEt.text}"
                ) {
                    resetMpinViewModel.resetForgetMpin(
                        (activity as MainActivity).appSharedPref,
                        ReqResetForgetPin(
                            confirmMPin = "${binding.pinOneCnfEt.text}${binding.pinTwoCnfEt.text}${binding.pinThreeCnfEt.text}${binding.pinFourCnfEt.text}",
                            mobileNo = (activity as MainActivity).appSharedPref?.getStringValue(
                                com.paulmerchants.gold.utility.Constants.CUST_MOBILE
                            ).toString(),
                            newMPin = "${binding.pinOneNewEt.text}${binding.pinTwoNewEt.text}${binding.pinThreeNewEt.text}${binding.pinFourNewEt.text}",
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

    private fun validateAllFieldForReset(): Boolean {
        return binding.pinOneNewEt.text.isNotEmpty() &&
                binding.pinTwoNewEt.text.isNotEmpty() &&
                binding.pinThreeNewEt.text.isNotEmpty() &&
                binding.pinFourNewEt.text.isNotEmpty() &&
                binding.pinOneCnfEt.text.isNotEmpty() &&
                binding.pinTwoCnfEt.text.isNotEmpty() &&
                binding.pinThreeCnfEt.text.isNotEmpty() &&
                binding.pinFourCnfEt.text.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }

    override fun setStyle(style: Int, theme: Int) {
        super.setStyle(style, theme)
    }


    private fun focusAllEt() {
        newMpinNextFocus()
        confirmMpinConfirmNextFocus()
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
                        binding.proceedAuthBtn.enableButton(requireContext())
//                                    binding.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.proceedAuthBtn.disableButton(requireContext())
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
                        binding.proceedAuthBtn.enableButton(requireContext())
//                                    binding.signUpBtn.enableButton(requireContext())
                        nextView?.requestFocus()
                    } else {
                        binding.proceedAuthBtn.disableButton(requireContext())
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