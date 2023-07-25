package com.paulmerchants.gold.ui.others

import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.LayoutLoanEmiProceedToPayBinding
import com.paulmerchants.gold.databinding.LoanEmiPaymentConfirmedBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PaymentConfirmed :
    BaseFragment<LoanEmiPaymentConfirmedBinding>(LoanEmiPaymentConfirmedBinding::inflate) {
    var headerValue :String? = null
    override fun LoanEmiPaymentConfirmedBinding.initialize() {
        headerValue = arguments?.getString(Constants.BBPS_HEADER, "")

    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            headerLoanConfirmed.backIv.hide()
            headerLoanConfirmed.endIconIv.show()
            headerLoanConfirmed.titlePageTv.setText(headerValue.toString())
            headerLoanConfirmed.endIconIv.setImageResource(R.drawable.bbps)
            gotoHomeBtn.setOnClickListener {
      }
            lifecycleScope.launch {
                delay(2000)
                loadingParent.hide()
                paymentConfirmedParent.show()
                paymentConfirmedParent.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(), R.anim.slide_up
                        ))
            }

        }
    }
}