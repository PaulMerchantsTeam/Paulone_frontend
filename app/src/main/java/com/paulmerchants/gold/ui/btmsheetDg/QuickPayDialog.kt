package com.paulmerchants.gold.ui.btmsheetDg

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.QuickPayPopupBinding
import com.paulmerchants.gold.model.DueLoans
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

    lateinit var quickPayPopupBinding: QuickPayPopupBinding
    val TAG = "QuickPayDialog"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        quickPayPopupBinding = QuickPayPopupBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ")
        return quickPayPopupBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dueLoans = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelable(
                Constants.DUE_LOAN_DATA,
                DueLoans::class.java
            ) else arguments?.getParcelable<DueLoans>(Constants.DUE_LOAN_DATA) as DueLoans
        Log.d(TAG, "onCreate:---dueDays-${dueLoans?.dueDays}\n-----amount---${dueLoans?.amount} ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quickPayPopupBinding.quixPayParentBtn.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.quickPayMainFrag)
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