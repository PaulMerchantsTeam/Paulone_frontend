package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.databinding.ItemLastStatementBinding
import com.paulmerchants.gold.model.ActionItem

class LastStatemnetAdapter :
    ListAdapter<ActionItem, LastStatemnetAdapter.LastStatementHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LastStatementHolder(
        ItemLastStatementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: LastStatementHolder, position: Int) {
        holder.bindLast(getItem(position))
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

    inner class LastStatementHolder(private val binding: ItemLastStatementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLast(actionItem: ActionItem) {
            binding.apply {
                binding.apply {
                    binding.dateValue.text = actionItem.name
                }
            }
        }
    }


}