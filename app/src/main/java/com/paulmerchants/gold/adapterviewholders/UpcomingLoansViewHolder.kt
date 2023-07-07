package com.paulmerchants.gold.adapterviewholders

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.databinding.ItemUpcomingDueLoanBinding
import com.paulmerchants.gold.model.DueLoans
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem

class UpcomingLoansViewHolder(private val binding: ItemUpcomingDueLoanBinding) :
    ViewHolder(binding.root) {

    fun bind(
        dueLoans: GetPendingInrstDueRespItem,
        onPayDueClicked: (GetPendingInrstDueRespItem) -> Unit,
    ) {
        binding.apply {
            overDueDaysTv.text = "Overdue by ${dueLoans.DueDate} days"
            dueAmountTv.text = "INR ${dueLoans.InterestDue}"
        }
        binding.payNowBtn.setOnClickListener {
            onPayDueClicked(dueLoans)
        }
    }
}