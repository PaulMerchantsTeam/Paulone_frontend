package com.paulmerchants.gold.ui.others

import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.RegisterComplaintBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.utility.showCustomDialogOTPVerify
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RaiseComplaint : BaseFragment<RegisterComplaintBinding>(RegisterComplaintBinding::inflate) {

    override fun RegisterComplaintBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        handleClicks()

    }

    private fun handleClicks() {
        binding.apply {
            getOtpBtn.setOnClickListener {
                showCustomDialogOTPVerify(requireContext())
                RegisterComplaintParent.show()
                getOtpBtn.hide()
                submitBtn.show()
                trackComplaint.show()
            }
            submitBtn.setOnClickListener {
                ComplaintSuccessFullyRegisterParent.show()
                complaintTypeTv.hide()
                complaintTypeParent.hide()
                RegisterComplaintParent.hide()
                mobileNoTv.hide()
                mobileNumEt.hide()
                getOtpBtn.hide()
                submitBtn.hide()
                trackComplaint.hide()
            }
        }
    }
}