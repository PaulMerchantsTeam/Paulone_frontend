package com.paulmerchants.gold.ui.others

import com.paulmerchants.gold.R
import android.widget.ArrayAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.LoanStatementBinding
import com.paulmerchants.gold.utility.hide
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoanStatementScreenFrag : BaseFragment<LoanStatementBinding>(LoanStatementBinding::inflate) {

    override fun LoanStatementBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()
        addEntriesToSpinners()
//        setLastStatement()
    }

    private fun modifyHeaders() {
        binding.headerScrn.backIv.hide()
        binding.headerScrn.titlePageTv.text = getString(R.string.loan_Statment)
    }

    private fun addEntriesToSpinners() {
        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.month, R.layout.spinner_item_text
        )
        val adapter2: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.year, R.layout.spinner_item_text
        )
        binding.monthSpinner.adapter = adapter
        binding.yrSpinner.adapter = adapter2
    }

//    private fun setLastStatement() {
//        binding.rvLastTrans.setUiOnLastTransaction()
//    }

}