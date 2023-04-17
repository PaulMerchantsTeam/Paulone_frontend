package com.paulmerchants.gold.ui.others

import androidx.lifecycle.lifecycleScope
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.CreditScoreScreenBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CreditScoreScreenFrag : BaseFragment<CreditScoreScreenBinding>(CreditScoreScreenBinding::inflate) {

    override fun CreditScoreScreenBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()

        binding.apply {
            saveCardBtn.setOnClickListener {
                rvAllAboutCreditScore.hide()
                creditScoreParent.hide()
                constraintLayout10.hide()
                basicDetailsParent.show()
            }
            proceedAuthBtn.setOnClickListener {
                basicDetailsParent.hide()
                checkCreditTv.show()
                constraintLayout10.show()
                creditScoreParent.show()
                aboutCreditScore.text = getString(R.string.overview)
                aboutCreditScore.setTextColor(resources.getColor(R.color.splash_screen_one))
                billTimelyLimitedTv.show()
                billTimelyLimitedTv.text = getString(R.string.timely_bill_paid)
                lifecycleScope.launch {
    delay(1500)
    cardViewParent.show()
    creditScoreParent.show()
    mainCreditScoreParent.setBackgroundColor(resources.getColor(R.color.splash_screen_two))

}
            }
        }
    }

    private fun modifyHeaders() {
        binding.headerScrn.titlePageTv.text = getString(R.string.credit_score_caps)

    }



}