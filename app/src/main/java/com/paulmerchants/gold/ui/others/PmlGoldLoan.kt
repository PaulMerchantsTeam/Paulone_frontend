package com.paulmerchants.gold.ui.others

import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.PmlGoldLoanBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PmlGoldLoan : BaseFragment<PmlGoldLoanBinding>(PmlGoldLoanBinding::inflate) {

    override fun PmlGoldLoanBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        binding.apply {
            dueLoanParent.viewStateMent.setOnClickListener {

                findNavController().navigate(R.id.loanStatementFrag)
            }
        }
    }

}