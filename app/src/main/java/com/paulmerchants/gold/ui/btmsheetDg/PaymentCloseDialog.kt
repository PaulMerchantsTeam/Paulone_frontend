package com.paulmerchants.gold.ui.btmsheetDg

//import com.razorpay.Checkout
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.AppCloseDialogBinding
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
class PaymentCloseDialog : BottomSheetDialogFragment() {
    lateinit var quickPayPopupBinding: AppCloseDialogBinding
    val TAG = "AppCloseDialog"
//    private var isForPaymentStatus: Boolean? = null
//    private var paymentStatus: Boolean? = null

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
        quickPayPopupBinding = AppCloseDialogBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ")
        quickPayPopupBinding.apply {
            quickPay.text = "Do you want to cancel the Payment?"
        }
        return quickPayPopupBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        isForPaymentStatus = arguments?.getBoolean("IS_FOR_PAYMENT_STATUS")
//        isForPaymentStatus = arguments?.getBoolean("PAYMENT_STATUS")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quickPayPopupBinding.loginParentBtn.setOnClickListener {
            dismiss()
            findNavController().popBackStack(R.id.paymentModesFragNew, true)
            findNavController().navigate(R.id.homeScreenFrag)
        }

        quickPayPopupBinding.cancelDgBtn.setOnClickListener {
            dismiss()
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

    }

    override fun setStyle(style: Int, theme: Int) {
        super.setStyle(style, theme)
    }

}