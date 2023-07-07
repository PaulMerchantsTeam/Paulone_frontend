package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.paulmerchants.gold.adapterviewholders.UpcomingLoansViewHolder
import com.paulmerchants.gold.databinding.ItemUpcomingDueLoanBinding
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem

class UpcomingLoanAdapter(private val onPayDueClicked: (GetPendingInrstDueRespItem) -> Unit) :
    ListAdapter<GetPendingInrstDueRespItem, UpcomingLoansViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UpcomingLoansViewHolder(
        ItemUpcomingDueLoanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: UpcomingLoansViewHolder, position: Int) {
        holder.bind(getItem(position), onPayDueClicked)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<GetPendingInrstDueRespItem>() {
                override fun areItemsTheSame(
                    oldItem: GetPendingInrstDueRespItem,
                    newItem: GetPendingInrstDueRespItem,
                ): Boolean = true

                override fun areContentsTheSame(
                    oldItem: GetPendingInrstDueRespItem,
                    newItem: GetPendingInrstDueRespItem,
                ): Boolean = true
            }
    }


}