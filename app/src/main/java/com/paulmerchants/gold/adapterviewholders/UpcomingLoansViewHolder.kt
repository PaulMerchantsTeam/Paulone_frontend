package com.paulmerchants.gold.adapterviewholders

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.databinding.ItemUpcomingDueLoanBinding
import com.paulmerchants.gold.model.DueLoans

class UpcomingLoansViewHolder(private val binding: ItemUpcomingDueLoanBinding) :
    ViewHolder(binding.root) {

    fun bind(dueLoans: DueLoans, onPayDueClicked: (DueLoans) -> Unit) {
        binding.apply {
            overDueDaysTv.text = "Overdue by ${dueLoans.dueDays} days"
            dueAmountTv.text = "INR ${dueLoans.amount}"
        }
        binding.payNowBtn.setOnClickListener {
            onPayDueClicked(dueLoans)
        }
    }
}