package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.databinding.ItemLoansOverViewBinding
import com.paulmerchants.gold.model.ActionItem

class GoldLoanOverViewAdapter(private val buttonClicked : (ActionItem) ->Unit) :
    ListAdapter<ActionItem, GoldLoanOverViewAdapter.GoldLoanOverViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GoldLoanOverViewHolder(
        ItemLoansOverViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GoldLoanOverViewHolder, position: Int) {
        holder.bindLast(getItem(position),buttonClicked)
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

    inner class GoldLoanOverViewHolder(private val binding: ItemLoansOverViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLast(actionItem: ActionItem,buttonClicked : (ActionItem) ->Unit) {
            binding.apply {
                binding.apply {
                    binding.loanNumTv.text = actionItem.name
                    viewDetailsBtn.setOnClickListener {
                        buttonClicked(actionItem)
                    }
                }
            }
        }
    }


}