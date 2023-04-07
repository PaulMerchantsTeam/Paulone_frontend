package com.paulmerchants.gold.ui.others

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.CheckOutWindowFrPaymentBinding
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class QuickPayMainFrag :
    BaseFragment<CheckOutWindowFrPaymentBinding>(CheckOutWindowFrPaymentBinding::inflate) {

    override fun CheckOutWindowFrPaymentBinding.initialize() {
        (activity as MainActivity).changeHeader(
            binding.headerCheckOutQuickPay,
            getString(R.string.checkout),
            R.drawable.quest_circle
        )

    }

    override fun onStart() {
        super.onStart()
        navigateToOtherScrn()
    }

    private fun navigateToOtherScrn() {
        binding.apply {
            confirmBtn.setOnClickListener {
                binding.apply {
                    checkOutParent.hide()
                    lifecycleScope.launchWhenCreated {
                        loadingParent.show()
                        delay(2000)
                        loadingParent.hide()
                        parent.setBackgroundResource(R.color.color_prim_one_40)
                        delay(500)
                        parent.setBackgroundResource(R.color.splash_screen_one)
                        delay(2000)
                        binding.payConfirmedParentAnim.show()
                        delay(2000)
                        findNavController().navigate(R.id.confirmPaymentDueFrag)
                    }
                }

            }
        }
    }

    private fun showSuccessDialog() {


    }

}