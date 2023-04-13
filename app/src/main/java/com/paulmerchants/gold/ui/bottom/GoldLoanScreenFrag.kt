package com.paulmerchants.gold.ui.bottom

import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.GoldLoanScreenFragmentBinding
import com.paulmerchants.gold.databinding.MainScreenFragmentBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.setGoldLoanOverView
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoldLoanScreenFrag :
    BaseFragment<GoldLoanScreenFragmentBinding>(GoldLoanScreenFragmentBinding::inflate) {

    override fun GoldLoanScreenFragmentBinding.initialize() {
        binding.headerBillMore.apply {
            backIv.hide()
            titlePageTv.text = getString(R.string.loan_overview)
            subTitle.hide()
        }
        setUiForGoldLoan()
    }

    private fun setUiForGoldLoan() {
        binding.goldLoanParentMain.rvLoanOverViewMain.setGoldLoanOverView()
    }

}