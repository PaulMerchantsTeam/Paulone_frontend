package com.paulmerchants.gold.ui.others

import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.LayoutLoanEmiProceedToPayBinding
import com.paulmerchants.gold.databinding.LoanPayInrSecureBinding
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class AddCardFrag :
    BaseFragment<LoanPayInrSecureBinding>(LoanPayInrSecureBinding::inflate) {
    override fun LoanPayInrSecureBinding.initialize() {

    }
}