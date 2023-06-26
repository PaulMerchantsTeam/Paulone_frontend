package com.paulmerchants.gold.ui

import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.CreditScoreScreenBinding
import com.paulmerchants.gold.databinding.MainScreenFragmentBinding
import com.paulmerchants.gold.utility.hide
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainScreenFrag : BaseFragment<MainScreenFragmentBinding>(MainScreenFragmentBinding::inflate) {

    override fun MainScreenFragmentBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()

    }

    private fun modifyHeaders() {
//        binding.headerScrn.titlePageTv.text = getString(R.string.credit_score_caps)
    }


}