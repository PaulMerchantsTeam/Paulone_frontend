package com.paulmerchants.gold.ui.others

import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.LoanStatementBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoanStatementFrag : BaseFragment<LoanStatementBinding>(LoanStatementBinding::inflate) {

    override fun LoanStatementBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        binding.apply {

        }
    }

}