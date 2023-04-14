package com.paulmerchants.gold.ui.others

import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.CreditScoreScreenBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreditScoreScreenFrag : BaseFragment<CreditScoreScreenBinding>(CreditScoreScreenBinding::inflate) {

    override fun CreditScoreScreenBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()

    }

    private fun modifyHeaders() {
        binding.headerScrn.titlePageTv.text = getString(R.string.credit_score_caps)
    }


}