package com.paulmerchants.gold.ui.others

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.LayoutLoanEmiProceedToPayBinding
import com.paulmerchants.gold.databinding.LoanPayInrSecureBinding
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class AddCardFrag :
    BaseFragment<LoanPayInrSecureBinding>(LoanPayInrSecureBinding::inflate) {
    private var headerValue: String? = null

    override fun LoanPayInrSecureBinding.initialize() {
        headerValue = arguments?.getString(Constants.BBPS_HEADER, "")

    }

    override fun onResume() {
        super.onResume()
        val headerBundle = Bundle().apply {
            putString(Constants.BBPS_HEADER,headerValue)
        }
        binding.apply {
            headerCredit.titlePageTv.setText(headerValue.toString())
            headerCredit.endIconIv.show()
            headerCredit.endIconIv.setImageResource(R.drawable.bbps)
            proceedToPayBtn.setOnClickListener {

                findNavController().navigate(R.id.paymentConfirmed,headerBundle)

            }
        }
    }
}