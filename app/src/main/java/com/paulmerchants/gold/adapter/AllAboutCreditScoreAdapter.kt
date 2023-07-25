package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.databinding.ItemAllAboutCreditScoreBinding
import com.paulmerchants.gold.model.AllAboutCred
import com.paulmerchants.gold.utility.AppUtility

class AllAboutCreditScoreAdapter :
    ListAdapter<AllAboutCred, AllAboutCreditScoreAdapter.GoldLoanOverViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GoldLoanOverViewHolder(
        ItemAllAboutCreditScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GoldLoanOverViewHolder, position: Int) {
        holder.bindLast(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AllAboutCred>() {
            override fun areItemsTheSame(
                oldItem: AllAboutCred,
                newItem: AllAboutCred,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: AllAboutCred,
                newItem: AllAboutCred,
            ): Boolean = oldItem == newItem
        }
    }

    inner class GoldLoanOverViewHolder(private val binding: ItemAllAboutCreditScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLast(item: AllAboutCred) {
            binding.apply {
                whatItIs.text = item.title
                AppUtility.diffColorText(item.desc1, item.desc2, item.desc3, "", "", "", descCredTv)
            }
        }
    }


}