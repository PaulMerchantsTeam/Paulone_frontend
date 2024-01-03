package com.paulmerchants.gold.ui.others

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.PmlGoldLoanBinding
import com.paulmerchants.gold.model.DueLoans
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem
import com.paulmerchants.gold.utility.AppUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue


@AndroidEntryPoint
class PmlGoldLoan : BaseFragment<PmlGoldLoanBinding>(PmlGoldLoanBinding::inflate) {
    private lateinit var dueLoans: DueLoans
    var closedDate: String? = null
    override fun PmlGoldLoanBinding.initialize() {
        dueLoans = DueLoans(1, 12, 2000)
    }

    override fun onStart() {
        super.onStart()
        val loanOutStanding = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelable(
                Constants.LOAN_OVERVIEW,
                RespGetLoanOutStandingItem::class.java
            ) else arguments?.getParcelable<RespGetLoanOutStandingItem>(Constants.LOAN_OVERVIEW) as RespGetLoanOutStandingItem
        setData(loanOutStanding)

        binding.apply {
            dueLoanParent.viewStateMent.setOnClickListener {
                findNavController().navigate(R.id.loanStatementFrag)
            }
            dueLoanParent.payNowBtn.setOnClickListener {
                onPayDueClicked(dueLoans)
            }
        }
    }

    private fun setData(loanOutStanding: RespGetLoanOutStandingItem?) {
        binding.apply {
            loanNumTv.text = loanOutStanding?.AcNo.toString()
            intPeriodNumTv.text = "${loanOutStanding?.InterestPeriod} days"
            outStandLoanNumTv.text = "INR ${loanOutStanding?.OutStanding}"
            if (loanOutStanding?.ClosedDate != null && loanOutStanding.ClosedDate !="") {
                closedDate = AppUtility.getDateFormat(loanOutStanding.ClosedDate)
            } else {
                closedDate = "NA"
            }
            loanPeriodNumTv.text =
                "${AppUtility.getDateFormat(loanOutStanding?.OpenDate.toString())}-$closedDate"
            loanPricepalNumTv.text = "NA"
            loanJourneyMainTitle.text = "Loan Journey as of ${AppUtility.getCurrentDate()}"
            loanStartedDateTv.text =
                "${AppUtility.getDateFormat(loanOutStanding?.OpenDate.toString())}"
            loanStartedDateTv2.text = closedDate
            loanOutStanding?.OpeningAmount?.let {
                linearProgressIndicator.max = it.toInt()
            }
            val left =
                loanOutStanding?.OutStanding?.let { loanOutStanding.OpeningAmount?.minus(it) }
            if (left != null) {
                linearProgressIndicator.progress = left.toInt()
                paidAmountLoanTV.text = "Paid - INR $left"
                //for pg icon ....set Some Logic to render at any position
            } else {
                linearProgressIndicator.progress = 1
                paidAmountLoanTV.text = "Paid - INR 0"
                pgIcon.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0.0f }
            }
        }


        binding.dueLoanParent.apply {

            val duedate = AppUtility.numberOfDaysWrtCurrent(loanOutStanding?.DueDate.toString())
            when {
                duedate.toInt() < 0 -> {
                    Log.d("TAG", "bind: ----< than 0")
                    ovrDueParentArrow.setBackgroundResource(R.drawable.rect_due_green)
                    overDueDaysTv.text = "Due in ${duedate.absoluteValue} days"
                }

                else -> {
                    Log.d("TAG", "bind: ----else ---- ")
                    ovrDueParentArrow.setBackgroundResource(R.drawable.rectangle_due_red)
                    overDueDaysTv.text = "Overdue by $duedate days"
                }
            }

            dueAmountTv.text = "INR ${loanOutStanding?.OutStanding}"
        }
    }

    private fun onPayDueClicked(dueLoans: DueLoans) {
        val bundle = Bundle().apply {
            putParcelable(Constants.DUE_LOAN_DATA, dueLoans)
        }
        findNavController().navigate(
            R.id.quickPayDialog,
            bundle
        )
    }

}