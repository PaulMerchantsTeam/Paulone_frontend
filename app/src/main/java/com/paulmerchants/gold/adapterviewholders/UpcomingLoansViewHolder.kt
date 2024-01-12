package com.paulmerchants.gold.adapterviewholders

import android.util.Log
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.ItemUpcomingDueLoanBinding
import com.paulmerchants.gold.model.DueLoans
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.utility.AppUtility
import kotlin.math.absoluteValue

class UpcomingLoansViewHolder(private val binding: ItemUpcomingDueLoanBinding) :
    ViewHolder(binding.root) {

    fun bind(
        dueLoans: GetPendingInrstDueRespItem,
        onPayDueClicked: (GetPendingInrstDueRespItem) -> Unit,
    ) {
        Log.d("TAG", "bind: ............$dueLoans")
        binding.apply {
//            val duedate = dueLoans.dueDate?.let { AppUtility.numberOfDaysWrtCurrent(it) }
            val duedate = AppUtility.getCurrentDateOnly()
            overDueDaysTv.text = "Due till date\n$duedate"
         /*   when {
                (duedate?.toInt() ?: 0) < 0 -> {
                    Log.d("TAG", "bind: ----< than 0")
                    ovrDueParentArrow.setBackgroundResource(R.drawable.rect_due_green)
                    overDueDaysTv.text = "Due in $duedate"
                }

                duedate == null -> {
                    overDueDaysTv.text = ""
                }

                else -> {
                    Log.d("TAG", "bind: ----else ---- ")
                    ovrDueParentArrow.setBackgroundResource(R.drawable.rectangle_due_red)
                    overDueDaysTv.text = "Overdue by $duedate days"
                }
            }*/
//            if (dueLoans.payableAmount > 0.000) {
            dueAmountTv.text = "INR ${dueLoans.payableAmount}"
//            } else {
//                dueAmountTv.text = "INR ${dueLoans.InterestDue}"
//            }
        }
        binding.payNowBtn.setOnClickListener {
            onPayDueClicked(dueLoans)
        }
    }
}