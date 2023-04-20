package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.databinding.YourCardsLayoutBinding
import com.paulmerchants.gold.model.DueLoans
import com.paulmerchants.gold.model.PrepaidCardModel
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.invisible

class PrePaidCardAdapter(private val onPayDueClicked: (PrepaidCardModel) -> Unit) :
    ListAdapter<PrepaidCardModel, PrePaidCardAdapter.PrepaidCardVIewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PrepaidCardVIewHolder(
        YourCardsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: PrepaidCardVIewHolder, position: Int) {
        holder.bind(getItem(position), position,onPayDueClicked)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<PrepaidCardModel>() {
                override fun areItemsTheSame(
                    oldItem: PrepaidCardModel,
                    newItem: PrepaidCardModel,
                ): Boolean =
                    oldItem.itemId == newItem.itemId

                override fun areContentsTheSame(
                    oldItem: PrepaidCardModel,
                    newItem: PrepaidCardModel,
                ): Boolean =
                    oldItem == newItem
            }
    }

    inner class PrepaidCardVIewHolder(private val binding: YourCardsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dueLoans: PrepaidCardModel, position: Int,clicked: (PrepaidCardModel) -> Unit) {
            binding.apply {
                cardHolderName.text = dueLoans.name
                viewLoanBtn.setOnClickListener {
                    clicked(dueLoans)
                }
            }
        }
    }


}