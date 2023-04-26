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
        setUiFoOpenGoldLoans()
        setClickListener()
    }

    private fun setUiForClosedGoldLoans() {
        binding.goldLoanParentMain.rvLoanOverViewMain.setGoldLoanOverView(0)
    }

    private fun setUiFoOpenGoldLoans() {
        binding.goldLoanParentMain.rvLoanOverViewMain.setGoldLoanOverView(1)
    }


    private fun setClickListener() {
        binding.apply {
            goldLoanParentMain.openLoanTv.setOnClickListener {
                goldLoanParentMain.openLoanTv.setBackgroundResource(R.drawable.rec_sky_loan_blue_solid)
                goldLoanParentMain.closedLoanTv.setBackgroundColor(resources.getColor(R.color.splash_screen_two))
                setUiFoOpenGoldLoans()
            }

            goldLoanParentMain.closedLoanTv.setOnClickListener {
                goldLoanParentMain.closedLoanTv.setBackgroundResource(R.drawable.rec_sky_loan_blue_solid)
                goldLoanParentMain.openLoanTv.setBackgroundColor(resources.getColor(R.color.splash_screen_two))
                setUiForClosedGoldLoans()
            }
        }

    }

}