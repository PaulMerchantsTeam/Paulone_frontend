package com.paulmerchants.gold.ui.others

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.PmlGoldLoanBinding
import com.paulmerchants.gold.model.DueLoans
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PmlGoldLoan : BaseFragment<PmlGoldLoanBinding>(PmlGoldLoanBinding::inflate) {
 private lateinit var dueLoans:DueLoans
    override fun PmlGoldLoanBinding.initialize() {
dueLoans = (DueLoans(1,12,2000))
    }

    override fun onStart() {
        super.onStart()
        binding.apply {
            dueLoanParent.viewStateMent.setOnClickListener {

                findNavController().navigate(R.id.loanStatementFrag)
            }
            dueLoanParent.payNowBtn.setOnClickListener {

                onPayDueClicked(dueLoans)
            }
        }
    }
    private fun onPayDueClicked(dueLoans: DueLoans) {
        val bundle = Bundle().apply {
            putParcelable(Constants.DUE_LOAN_DATA, dueLoans)
        }
        findNavController().navigate(
            R.id.quickPayDialog,
            bundle
        )
    }

}