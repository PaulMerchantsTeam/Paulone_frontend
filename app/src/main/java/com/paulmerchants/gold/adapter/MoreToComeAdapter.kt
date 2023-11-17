package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.paulmerchants.gold.databinding.ItemMoreToComeBinding
import com.paulmerchants.gold.model.MoreToComeModel

class MoreToComeAdapter :
    ListAdapter<MoreToComeModel, MoreToComeAdapter.GoldLoanOverViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GoldLoanOverViewHolder(
        ItemMoreToComeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GoldLoanOverViewHolder, position: Int) {
        holder.bindLast(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MoreToComeModel>() {
            override fun areItemsTheSame(
                oldItem: MoreToComeModel,
                newItem: MoreToComeModel,
            ): Boolean = oldItem.bannerId == newItem.bannerId

            override fun areContentsTheSame(
                oldItem: MoreToComeModel,
                newItem: MoreToComeModel,
            ): Boolean = oldItem == newItem
        }
    }

    inner class GoldLoanOverViewHolder(private val binding: ItemMoreToComeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindLast(item: MoreToComeModel) {
            binding.apply {
                Glide.with(binding.root.context).load(item.bannerImage).into(ivBanner)
            }
        }
    }


}