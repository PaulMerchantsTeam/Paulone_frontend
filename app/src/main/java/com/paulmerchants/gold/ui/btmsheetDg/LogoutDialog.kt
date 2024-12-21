package com.paulmerchants.gold.ui.btmsheetDg

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.LogoutDialogBinding
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.viewmodels.CommonViewModel
import com.paulmerchants.gold.viewmodels.ProfileViewModel
//import com.razorpay.Checkout
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
class LogoutDialog : BottomSheetDialogFragment() {
    private val profileViewModel: ProfileViewModel by viewModels()
    private val commonViewModel: CommonViewModel by viewModels()
    lateinit var quickPayPopupBinding: LogoutDialogBinding
    val TAG = "LogoutDialog"

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

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quickPayPopupBinding.loginParentBtn.setOnClickListener {
            profileViewModel.logout(
               context =  requireContext()
            )

        }

        quickPayPopupBinding.cancelDgBtn.setOnClickListener {
            dismiss()
        }

        profileViewModel.logoutLiveData.observe(viewLifecycleOwner){
            if(it.status_code == 200){
                val bundle = Bundle().apply {
                    putBoolean(Constants.IS_LOGOUT, true)
                }
                findNavController().popBackStack(R.id.homeScreenFrag, true)
                findNavController().popBackStack(R.id.profileFrag, true)
                findNavController().navigate(R.id.phoenNumVerifiactionFragment, bundle)
                "${it?.message}".showSnackBar()
            }
            else if (it.status_code== 498){
                commonViewModel.refreshToken(requireContext())
            }
            else{
                "${it?.message}".showSnackBar()
            }

        }
        commonViewModel.refreshTokenLiveData.observe(viewLifecycleOwner){
            if(it.status_code == 200){
                profileViewModel.logout(progress = false,
                    requireContext()
                )
            }

            else{
                "${it?.message}".showSnackBar()
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


    }

    override fun setStyle(style: Int, theme: Int) {
        super.setStyle(style, theme)
    }

}