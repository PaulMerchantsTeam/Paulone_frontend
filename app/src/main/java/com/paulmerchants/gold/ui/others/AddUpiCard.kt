package com.paulmerchants.gold.ui.others

import com.paulmerchants.gold.R
import android.widget.ArrayAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.LoanStatementBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.setUiOnLastTransaction
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddUpiCard : BaseFragment<LoanStatementBinding>(LoanStatementBinding::inflate) {

    override fun LoanStatementBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()

    }

    private fun modifyHeaders() {
        binding.headerScrn.backIv.hide()
        binding.headerScrn.titlePageTv.text = getString(R.string.loan_Statment)
    }



}