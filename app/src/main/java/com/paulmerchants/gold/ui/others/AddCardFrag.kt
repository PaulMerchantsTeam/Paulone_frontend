package com.paulmerchants.gold.ui.others

import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.LayoutLoanEmiProceedToPayBinding
import com.paulmerchants.gold.databinding.LoanPayInrSecureBinding
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class AddCardFrag :
    BaseFragment<LoanPayInrSecureBinding>(LoanPayInrSecureBinding::inflate) {
    override fun LoanPayInrSecureBinding.initialize() {

    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            proceedToPayBtn.setOnClickListener {

                findNavController().navigate(R.id.paymentConfirmed)

            }
        }
    }
}