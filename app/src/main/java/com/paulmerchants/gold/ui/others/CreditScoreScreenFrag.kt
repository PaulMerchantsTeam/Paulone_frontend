package com.paulmerchants.gold.ui.others

import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.AllAboutCreditScoreAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.CreditScoreScreenBinding
import com.paulmerchants.gold.model.AllAboutCred
import com.paulmerchants.gold.utility.AppUtility.blurTextView
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CreditScoreScreenFrag :
    BaseFragment<CreditScoreScreenBinding>(CreditScoreScreenBinding::inflate) {

    private val credAboutAdapter = AllAboutCreditScoreAdapter()
    override fun CreditScoreScreenBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
//        blurTextView(binding.creditScore, requireContext())
        modifyHeaders()
        setData()
        binding.apply {
            saveCardBtn.setOnClickListener {
                rvAllAboutCreditScore.hide()
                headerScrn.root.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.splash_screen_two
                    )
                )
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
                aboutCreditScore.setTextColor(ContextCompat.getColor(requireContext(),R.color.splash_screen_one))
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

    private fun setData() {
        val aboutList = listOf(
            AllAboutCred(
                1,
                "WHAT IS IT?",
                "A person's  ",
                "creditworthiness",
                " based on their credit history."
            ),
            AllAboutCred(
                2,
                "WHY IT IS?",
                "For lenders to  ",
                "assess ",
                "a borrower's creditworthiness"
            ),
            AllAboutCred(
                3,
                "HOW TO CHECK?",
                "By requesting a credit report from one of the  ",
                "credit bureaus ",
                "or by using a credit monitoring service"
            ),
            AllAboutCred(
                4,
                "WHEN TO CHECK?",
                "Check your credit score ",
                "regularly ",
                ", at least once a year"
            )
        )
        credAboutAdapter.submitList(aboutList)
        binding.rvAllAboutCreditScore.adapter = credAboutAdapter
    }


}