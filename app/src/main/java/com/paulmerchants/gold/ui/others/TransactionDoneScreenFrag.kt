package com.paulmerchants.gold.ui.others

import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.TransacReceiptBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDoneScreenFrag :
    BaseFragment<TransacReceiptBinding>(TransacReceiptBinding::inflate) {

    override fun TransacReceiptBinding.initialize() {
    }

    override fun onStart() {
        super.onStart()
        binding.headerMain.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.headerMain.titlePageTv.text = "Reliance Supermart Ltd"
    }
}