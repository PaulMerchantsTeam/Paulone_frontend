package com.paulmerchants.gold.ui.btmsheetDg

//import com.razorpay.Checkout
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.QuickPayPopupBinding
import com.paulmerchants.gold.model.responsemodels.PendingInterestDuesResponseData
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.ui.PaymentActivity
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.CUST_ACC
import com.paulmerchants.gold.utility.Constants.IS_CUSTOM_AMOUNT
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
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
class QuickPayDialog : BottomSheetDialogFragment() {

    private var dueLoans: PendingInterestDuesResponseData? = null
    lateinit var quickPayPopupBinding: QuickPayPopupBinding
    val TAG = "QuickPayDialog"
    private var actualLoan: Double? = 0.000

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        quickPayPopupBinding = QuickPayPopupBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ")
        return quickPayPopupBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dueLoans = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(
            Constants.DUE_LOAN_DATA, PendingInterestDuesResponseData::class.java
        ) else arguments?.getParcelable<PendingInterestDuesResponseData>(Constants.DUE_LOAN_DATA) as PendingInterestDuesResponseData

        Log.d(
            TAG,
            "onCreate:--=====--------$dueLoans-----------dueDays-${dueLoans?.due_date}\n-----amount---${dueLoans?.payable_amount} "
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAmount(dueLoans?.payable_amount.toString())
        quickPayPopupBinding.quixPayParentBtn.setOnClickListener {
            if (quickPayPopupBinding.customPayRadio.isChecked) {
                actualLoan?.let {
                    if (quickPayPopupBinding.customPayEt.text.isNotEmpty()) {
                        if (quickPayPopupBinding.customPayEt.text.toString().toDouble() > it) {
                            Toast.makeText(
                                requireContext(),
                                "Amount Due: ${
                                    dueLoans?.payable_amount
                                }\nPlease fill valid amount",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            /**
                             * // Your code when input is not empty, not zero, and not just multiple zeros
                            // This condition will handle inputs like "000", "0000", etc., excluding just zeros
                            // trimStart('0') removes leading zeros to check if there's a non-zero digit
                             */
                            if (quickPayPopupBinding.customPayEt.text.toString()
                                    .isNotEmpty() && quickPayPopupBinding.customPayEt.text.toString() != "0" &&
                                quickPayPopupBinding.customPayEt.text.trimStart('0') != ""
                            ) {
                                val bundle = Bundle().apply {
                                    putDouble(
                                        "AMOUNT_PAYABLE",
                                        quickPayPopupBinding.customPayEt.text.toString().toDouble()
                                    )
                                    putString(CUST_ACC, dueLoans?.ac_no.toString())
                                    putBoolean(IS_CUSTOM_AMOUNT, true)
                                }
                                findNavController().navigate(R.id.paymentModesFragNew, bundle)
                                dismiss()
                            } else {
                                "Please enter valid amount.".showSnackBar()
                            }

                        dismiss()

                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Amount cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                dismiss()
                dueLoans?.payable_amount?.let {
                    val bundle = Bundle().apply {
                        putDouble("AMOUNT_PAYABLE", it.toString().toDouble())
                        putString(CUST_ACC, dueLoans?.ac_no.toString())
                        putBoolean(IS_CUSTOM_AMOUNT, false)
                    }
                    val intent = Intent(requireContext(),  PaymentActivity ::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)


                }

            }
        }


        quickPayPopupBinding.apply {
            fullPayRadio.text =
                "Pay INR ${dueLoans?.payable_amount} fully"
            goldLoanNumTv.text = "Gold Loan - xxxx${dueLoans?.ac_no.toString().takeLast(4)}"
        }
        onCLickRadio()
    }




    fun setAmount(amount: String) {
        quickPayPopupBinding.payingAMountText.text = "Pay INR $amount"
    }

    fun onCLickRadio() {
        quickPayPopupBinding.fullPayRadio.setOnClickListener {
            quickPayPopupBinding.customPayEt.hide()
            dueLoans?.payable_amount?.toString()?.let { it1 -> setAmount(it1) }
        }
        quickPayPopupBinding.customPayRadio.setOnClickListener {
            quickPayPopupBinding.customPayEt.show()
            dueLoans?.payable_amount?.toString()?.let { it1 -> setAmount(it1) }
        }
        actualLoan = dueLoans?.payable_amount?.toString()?.toDouble()
        quickPayPopupBinding.customPayEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    setAmount(amount = p0.toString())
                }
            }

            override fun afterTextChanged(amount: Editable?) {


            }
        })

    }





    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as MainActivity).commonViewModel.responseCreateOrder.removeObservers(this)
        (activity as MainActivity).commonViewModel.responseCreateOrder.postValue(null)
    }

    override fun setStyle(style: Int, theme: Int) {
        super.setStyle(style, theme)
    }

}