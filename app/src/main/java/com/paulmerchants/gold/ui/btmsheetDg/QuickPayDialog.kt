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
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.QuickPayPopupBinding
import com.paulmerchants.gold.model.DueLoans
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.ui.MapActivity
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
            TAG, "onCreate:---dueDays-${dueLoans?.DueDate}\n-----amount---${dueLoans?.InterestDue} "
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAmount("${dueLoans?.InterestDue}")
        quickPayPopupBinding.quixPayParentBtn.setOnClickListener {
            if (quickPayPopupBinding.customPayRadio.isChecked) {
                dueLoans?.InterestDue?.toInt()?.let {
                    if (quickPayPopupBinding.customPayEt.text.toString().toInt() > it) {
                        Toast.makeText(
                            requireContext(),
                            "Amount Due: ${dueLoans?.InterestDue}\nPlease fill valid amount",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        dismiss()
                        findNavController().navigate(R.id.quickPayMainFrag)
                    }
                }
            } else {
                dismiss()
                findNavController().navigate(R.id.quickPayMainFrag)
            }

        }
        quickPayPopupBinding.chnagePayModeTv.setOnClickListener {
            findNavController().navigate(R.id.paymentModesFrag)
        }
        quickPayPopupBinding.apply {
            fullPayRadio.text = "Pay INR ${dueLoans?.InterestDue} fully"
        }
        onCLickRadio()

        quickPayPopupBinding.customPayEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {

                    setAmount(amount = p0.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    fun setAmount(amount: String) {
        quickPayPopupBinding.payingAMountText.text = "Pay INR $amount"
    }

    fun onCLickRadio() {
        quickPayPopupBinding.fullPayRadio.setOnClickListener {
            quickPayPopupBinding.customPayEt.hide()
            dueLoans?.InterestDue?.toString()?.let { it1 -> setAmount(it1) }
        }
        quickPayPopupBinding.customPayRadio.setOnClickListener {
            quickPayPopupBinding.customPayEt.show()
            dueLoans?.InterestDue?.toString()?.let { it1 -> setAmount(it1) }
        }
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
    }

    override fun setStyle(style: Int, theme: Int) {
        super.setStyle(style, theme)
    }
}