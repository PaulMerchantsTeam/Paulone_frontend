package com.paulmerchants.gold.ui.others

import com.paulmerchants.gold.R
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.AddCardUpiScreenBinding
import com.paulmerchants.gold.databinding.LoanStatementBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.setUiOnLastTransaction
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddUpiCard : BaseFragment<AddCardUpiScreenBinding>(AddCardUpiScreenBinding::inflate) {

    override fun AddCardUpiScreenBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()

    }

    private fun modifyHeaders() {
        binding.apply {
            include3.titlePageTv.text = getString(R.string.loan_Statment)
            addACardBtn.setOnClickListener {
                addACardDescParent.hide()
                cardDetailsParent.show()
            }
            saveCardBtn.setOnClickListener {
                findNavController().navigate(R.id.paymentModesFrag)
            }
        }

    }


}