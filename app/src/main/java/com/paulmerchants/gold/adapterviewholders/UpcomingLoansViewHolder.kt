package com.paulmerchants.gold.adapterviewholders

import android.util.Log
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.databinding.ItemUpcomingDueLoanBinding
import com.paulmerchants.gold.model.usedModels.GetPendingInrstDueRespItem

class UpcomingLoansViewHolder(private val binding: ItemUpcomingDueLoanBinding) :
    ViewHolder(binding.root) {

    fun bind(
        dueLoans: GetPendingInrstDueRespItem,
        onPayDueClicked: (GetPendingInrstDueRespItem) -> Unit,
    ) {
        Log.d("TAG", "bind: ............$dueLoans")
        binding.apply {

            overDueDaysTv.text = "Due till date\n${dueLoans.currentDate}"


            dueAmountTv.text = "INR ${dueLoans.payable_amount}"

        }
        binding.payNowBtn.setOnClickListener {
            onPayDueClicked(dueLoans)
        }
    }
}