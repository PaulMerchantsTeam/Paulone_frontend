package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.databinding.ItemLastStatementBinding
import com.paulmerchants.gold.model.RespLoanStatmentItem
import com.paulmerchants.gold.utility.AppUtility

class LastStatemnetAdapter :
    ListAdapter<RespLoanStatmentItem, LastStatemnetAdapter.LastStatementHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LastStatementHolder(
        ItemLastStatementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: LastStatementHolder, position: Int) {
        holder.bindLast(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RespLoanStatmentItem>() {
            override fun areItemsTheSame(
                oldItem: RespLoanStatmentItem,
                newItem: RespLoanStatmentItem,
            ): Boolean = oldItem.TransID == newItem.TransID

            override fun areContentsTheSame(
                oldItem: RespLoanStatmentItem,
                newItem: RespLoanStatmentItem,
            ): Boolean = oldItem == newItem
        }
    }

    inner class LastStatementHolder(private val binding: ItemLastStatementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLast(resp: RespLoanStatmentItem) {
            binding.apply {
                binding.apply {
                    dateValue.text = AppUtility.getDateMoth(resp.TransDate)
                    chargeValue.text = resp.OtherCharges.toString()
                    paymentValue.text = resp.Interest.toString()
                }
            }
        }
    }


}