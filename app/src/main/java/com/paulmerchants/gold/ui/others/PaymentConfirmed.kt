package com.paulmerchants.gold.ui.others

import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.LayoutLoanEmiProceedToPayBinding
import com.paulmerchants.gold.databinding.LoanEmiPaymentConfirmedBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentConfirmed :
    BaseFragment<LoanEmiPaymentConfirmedBinding>(LoanEmiPaymentConfirmedBinding::inflate) {
    override fun LoanEmiPaymentConfirmedBinding.initialize() {

    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            headerLoanConfirmed.backIv.hide()
            headerLoanConfirmed.endIconIv.show()
            headerLoanConfirmed.endIconIv.setImageResource(R.drawable.bbps_small)
        }
    }
}