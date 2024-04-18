package com.paulmerchants.gold.ui.others


import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.ApplyingForLoanLayoutBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApplyLoanForNewUser :
    BaseFragment<ApplyingForLoanLayoutBinding>(ApplyingForLoanLayoutBinding::inflate) {

    override fun ApplyingForLoanLayoutBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()
        binding.apply {
            header.endIconIv.apply {
                show()
                setBackgroundResource(R.drawable.cross_icon)
                setOnClickListener {
                    findNavController().navigateUp()
                }
            }
            availNowBtn.setOnClickListener {
                applyForLoanParent.hide()
                showingTv.show()
                oneConnectTv.show()
//                availNowBtn.text = getString(R.string.go_to_home)
//                binding.header.backIv.setOnClickListener {
//                    applyForLoanParent.show()
//                    showingTv.hide()
//                    oneConnectTv.hide()
//                    availNowBtn.text = getString(R.string.apply_now)
//                }
            }
        }


    }

    private fun modifyHeaders() {
        binding.header.backIv.hide()
        binding.header.titlePageTv.text = getString(R.string.apply_for_loan)
    }


}