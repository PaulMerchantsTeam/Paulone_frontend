package com.paulmerchants.gold.ui.btmsheetDg

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.LogoutDialogBinding
import com.paulmerchants.gold.databinding.QuickPayPopupBinding
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.newmodel.Notes
import com.paulmerchants.gold.model.newmodel.ReqCreateOrder
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.Constants.CUSTOMER_ID
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.CommonViewModel
//import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


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
class LogoutDialog : BottomSheetDialogFragment() {
    private var paymentResultListener: PaymentResultWithDataListener? = null

    private var dueLoans: GetPendingInrstDueRespItem? = null
    lateinit var quickPayPopupBinding: LogoutDialogBinding
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
        quickPayPopupBinding = LogoutDialogBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ")
        return quickPayPopupBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dueLoans = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(
            Constants.DUE_LOAN_DATA, GetPendingInrstDueRespItem::class.java
        ) else arguments?.getParcelable<GetPendingInrstDueRespItem>(Constants.DUE_LOAN_DATA) as GetPendingInrstDueRespItem
        Log.d(
            TAG, "onCreate:---dueDays-${dueLoans?.DueDate}\n-----amount---${dueLoans?.InterestDue} "
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quickPayPopupBinding.quixPayParentBtn.setOnClickListener {


        }
    }

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