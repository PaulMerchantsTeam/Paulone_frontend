package com.paulmerchants.gold.ui.others

import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.PaymentModesBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentModesFrag : BaseFragment<PaymentModesBinding>(PaymentModesBinding::inflate) {

    override fun PaymentModesBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()

    }

    private fun modifyHeaders() {
        binding.apply {
            binding.headerBillMore.titlePageTv.text = getString(R.string.credit_score_caps)
        }

    }


}