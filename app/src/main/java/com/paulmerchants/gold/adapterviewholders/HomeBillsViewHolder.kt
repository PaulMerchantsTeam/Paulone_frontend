package com.paulmerchants.gold.adapterviewholders

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.databinding.ItemActionBillBinding
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.utility.startCustomAnimation

class HomeBillsViewHolder(private val binding: ItemActionBillBinding) :
    ViewHolder(binding.root) {

    fun bind(actionItem: ActionItem, onBillClicked: (ActionItem) -> Unit) {
        binding.apply {

            binding.apply {

                iconIv.startCustomAnimation(actionItem.icon)
                binding.itemTv.text = actionItem.name
            }
            binding.apply {
                parentMainAction.setOnClickListener {
                    onBillClicked(actionItem)
                }
            }
        }
    }
}