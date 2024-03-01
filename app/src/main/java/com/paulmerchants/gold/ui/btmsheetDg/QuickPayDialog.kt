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
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.common.Constants.CUST_ACC
import com.paulmerchants.gold.common.Constants.IS_CUSTOM_AMOUNT
import com.paulmerchants.gold.databinding.QuickPayPopupBinding
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.ui.PaymentActivity
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
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

    private var dueLoans: GetPendingInrstDueRespItem? = null
    lateinit var quickPayPopupBinding: QuickPayPopupBinding
    val TAG = "QuickPayDialog"
    private var actualLoan: Double? = 0.000


//    // Method to set the listener
//    fun setPaymentResultListener(listener: PaymentResultWithDataListener) {
//        Log.d(TAG, "setPaymentResultListener: .............")
//        paymentResultListener = listener
//    }
//
//    // Methods inside the dialog where you trigger events
//    private fun notifyPaymentSuccess(p0: String?, p1: PaymentData?) {
//        paymentResultListener?.onPaymentSuccess(p0, p1)
//    }
//
//    private fun notifyPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
//        paymentResultListener?.onPaymentError(p0, p1, p2)
//    }

    //Pay INR 6,000 fully
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
            Constants.DUE_LOAN_DATA, GetPendingInrstDueRespItem::class.java
        ) else arguments?.getParcelable<GetPendingInrstDueRespItem>(Constants.DUE_LOAN_DATA) as GetPendingInrstDueRespItem

        Log.d(
            TAG,
            "onCreate:--=====--------$dueLoans-----------dueDays-${dueLoans?.dueDate}\n-----amount---${dueLoans?.payableAmount} "
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAmount(dueLoans?.payableAmount.toString())
        quickPayPopupBinding.quixPayParentBtn.setOnClickListener {
            if (quickPayPopupBinding.customPayRadio.isChecked) {
                actualLoan?.let {
                    if (quickPayPopupBinding.customPayEt.text.isNotEmpty()) {
                        if (quickPayPopupBinding.customPayEt.text.toString().toDouble() > it) {
                            Toast.makeText(
                                requireContext(),
                                "Amount Due: ${
                                    dueLoans?.payableAmount
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
                                    putString(CUST_ACC, dueLoans?.acNo.toString())
                                    putBoolean(IS_CUSTOM_AMOUNT, true)
                                }

                                findNavController().navigate(R.id.paymentModesFragNew, bundle)
                                dismiss()
                            } else {
                                "Please enter valid amount.".showSnackBar()
                            }
//                        createOrder(
//                            quickPayPopupBinding.customPayEt.text.toString().toDouble()
//                        )
//                        dismiss()
//                        findNavController().navigate(R.id.quickPayMainFrag)
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
//                dismiss()
                dueLoans?.payableAmount?.let {
                    val bundle = Bundle().apply {
                        putDouble("AMOUNT_PAYABLE", it)
                        putString(CUST_ACC, dueLoans?.acNo.toString())
                        putBoolean(IS_CUSTOM_AMOUNT, false)
                    }
                    val intent = Intent(requireContext(), PaymentActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
//                    findNavController().navigate(R.id.paymentModesFragNew, bundle)
//                    createOrder(it1)
                }

            }
        }

//        quickPayPopupBinding.chnagePayModeTv.setOnClickListener {
//            findNavController().navigate(R.id.paymentModesFrag)
//        }
        quickPayPopupBinding.apply {
            fullPayRadio.text =
                "Pay INR ${dueLoans?.payableAmount} fully"
            goldLoanNumTv.text = "Gold Loan - xxxx${dueLoans?.acNo.toString().takeLast(4)}"
        }
        onCLickRadio()
    }
    /*
        private fun createOrder(amount: Double) {
            Log.d(TAG, "createOrder: ......$amount")
            (activity as MainActivity).commonViewModel.createOrder(
                (activity as MainActivity).appSharedPref,
                reqCreateOrder = ReqCreateOrder(
                    amount = amount,
                    currency = "INR",
                    custId = (activity as MainActivity).appSharedPref?.getStringValue(CUSTOMER_ID)
                        .toString(),
                    notes = Notes("n_1_test", "n_2_test"),
                    receipt = "rec__",
                )
            )
            (activity as MainActivity).commonViewModel.responseCreateOrder.observe(viewLifecycleOwner) {
                it?.let {
                    if (it.statusCode == "200") {
                        (activity as MainActivity).amount = it.data.amount
    //                    startPaymentFromRazorPay(it.data.amount * 100, it.data.orderId)
                        dismiss()
                    }
                }
            }

        }*/


    /*    private fun startPaymentFromRazorPay(
            amount: Double,
            orderId: String,
        ) {
            val checkout = Checkout()
            checkout.setKeyID(BuildConfig.RAZORPAY_KEY)
            try {
                val options = JSONObject()
    //            if (paymentMethod == "upi") {
    //                if (validateUPI(upiEditText?.text.toString())) {
    //                    options.put("vpa", upiEditText?.text.toString())
    //                } else {
    //                    "UPI ID is not valid".showSnackBar(this)
    //                    return
    //                }
    //            }
                options.put("name", "Paul Merchants")
                options.put("description", "RefNo..")
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
                options.put("currency", "INR")
                options.put("amount", amount)
                options.put("order_Id", orderId)
    //            options.put("method", paymentMethod);
                val preFill = JSONObject()
                preFill.put("email", "kprithvi26@gmail.com")
                preFill.put("contact", "8968666401")
                options.put("prefill", preFill)
                options.put("theme", "#F9AC59")
    //            options.put("callback_url", callbaclUrl)
                options.put("key", BuildConfig.RAZORPAY_KEY);
    //            options.put("method", JSONObject().put("upi", true))

                Log.d(TAG, "startPaymentFromRazorPay: .......${options.toString()}")
                checkout.open(requireActivity(), options)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error in payment: " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }*/

    fun setAmount(amount: String) {
        quickPayPopupBinding.payingAMountText.text = "Pay INR $amount"
    }

    fun onCLickRadio() {
        quickPayPopupBinding.fullPayRadio.setOnClickListener {
            quickPayPopupBinding.customPayEt.hide()
            dueLoans?.payableAmount?.toString()?.let { it1 -> setAmount(it1) }
        }
        quickPayPopupBinding.customPayRadio.setOnClickListener {
            quickPayPopupBinding.customPayEt.show()
            dueLoans?.payableAmount?.toString()?.let { it1 -> setAmount(it1) }
        }
        actualLoan = dueLoans?.payableAmount
        quickPayPopupBinding.customPayEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    setAmount(amount = p0.toString())
                }
            }

            override fun afterTextChanged(amount: Editable?) {
//                actualLoan?.let {
//                    if (amount.toString() != "" && amount.toString().toDouble() > it) {
//                        Toast.makeText(
//                            requireContext(),
//                            "Amount should not be greater than interest",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }

            }
        })
//        quickPayPopupBinding.customPayEt.setOnClickListener {
//            if (quickPayPopupBinding.customPayEt.text.toString()
//                    .toInt() > dueLoans?.InterestDue?.toInt() as Int
//            ) {
//                Toast.makeText(requireContext(), "Please fill valid amount", Toast.LENGTH_SHORT)
//                    .show()
//            } else {
//                quickPayPopupBinding.payingAMountText.text =
//                    "Pay INR ${quickPayPopupBinding.customPayEt.text}"
//            }
//        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)

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
        (activity as MainActivity).commonViewModel.responseCreateOrder.removeObservers(this)
        (activity as MainActivity).commonViewModel.responseCreateOrder.postValue(null)
    }

    override fun setStyle(style: Int, theme: Int) {
        super.setStyle(style, theme)
    }

}