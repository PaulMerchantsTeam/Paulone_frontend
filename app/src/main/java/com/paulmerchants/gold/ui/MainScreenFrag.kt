package com.paulmerchants.gold.ui

import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.LauoutUnderMaintanceBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Dummy Sample for fragment
 */
@AndroidEntryPoint
class MainScreenFrag :
    BaseFragment<LauoutUnderMaintanceBinding>(LauoutUnderMaintanceBinding::inflate) {

    override fun LauoutUnderMaintanceBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()
        binding.closeBtn.setOnClickListener {
            (activity as MainActivity).finish()
        }

    }

    private fun modifyHeaders() {
//        binding.headerScrn.titlePageTv.text = getString(R.string.credit_score_caps)
    }


}