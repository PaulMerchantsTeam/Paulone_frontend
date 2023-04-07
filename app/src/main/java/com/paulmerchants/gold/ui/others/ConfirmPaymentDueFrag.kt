package com.paulmerchants.gold.ui.others

import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.MainScreenFragmentBinding
import com.paulmerchants.gold.databinding.PaymentConfirmedQkPyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmPaymentDueFrag :
    BaseFragment<PaymentConfirmedQkPyBinding>(PaymentConfirmedQkPyBinding::inflate) {

    override fun PaymentConfirmedQkPyBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        navigateScreen()
    }

    private fun navigateScreen() {
        binding.goToHomeBtn.setOnClickListener {
            findNavController().navigate(R.id.homeScreenFrag)
        }
    }

}