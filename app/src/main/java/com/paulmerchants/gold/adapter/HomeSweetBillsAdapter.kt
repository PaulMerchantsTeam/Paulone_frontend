package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.paulmerchants.gold.adapterviewholders.HomeBillsViewHolder
import com.paulmerchants.gold.databinding.ItemActionBillBinding
import com.paulmerchants.gold.model.ActionItem

class HomeSweetBillsAdapter(private val onBillClicked :(ActionItem) -> Unit) :
    ListAdapter<ActionItem, HomeBillsViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeBillsViewHolder(
        ItemActionBillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: HomeBillsViewHolder, position: Int) {
        holder.bind(getItem(position),onBillClicked)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<ActionItem>() {
                override fun areItemsTheSame(
                    oldItem: ActionItem,
                    newItem: ActionItem,
                ): Boolean =
                    oldItem.itemId == newItem.itemId

                override fun areContentsTheSame(
                    oldItem: ActionItem,
                    newItem: ActionItem,
                ): Boolean =
                    oldItem == newItem
            }
    }


}