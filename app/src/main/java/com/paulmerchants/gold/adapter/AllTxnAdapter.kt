package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.ItemTxnLayoutBinding
import com.paulmerchants.gold.model.responsemodels.Transactions
import com.paulmerchants.gold.utility.AppUtility

class AllTxnAdapter(private val showTxn: (Transactions) -> Unit) :
    PagingDataAdapter<Transactions, AllTxnAdapter.AllTxnAdapter>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AllTxnAdapter(
        ItemTxnLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: AllTxnAdapter, position: Int) {
        getItem(position)?.let { holder.bindLast(it, showTxn) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Transactions>() {
            override fun areItemsTheSame(
                oldItem: Transactions,
                newItem: Transactions,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Transactions,
                newItem: Transactions,
            ): Boolean = oldItem == newItem
        }
    }

    inner class AllTxnAdapter(private val binding: ItemTxnLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLast(item: Transactions, showTxn: (Transactions) -> Unit) {
            binding.apply {
                with(binding.transReferIdTv) {
                    text = buildString {
                        append("Amount: ${binding.root.context.getString(R.string.Rs)} ${item.amount}\n")
                        append("Customer Id: ${item.cust_id}\n")
                        append("Transaction Id: ${item.payment_id ?: ""}\n")
                        append("Receipt Id:  ${item.receipt_id}\n")
                        append("Date: ${AppUtility.formatDateFromMilliSec(item.created_at)}")
                    }
                }

                if (item.status == "PAID") {
                    binding.statusTv.text = item.status
                    binding.statusTv.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.green_success
                        )
                    )
                } else {
                    binding.statusTv.text = binding.root.context.getString(R.string.failed)
                    binding.statusTv.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.red
                        )
                    )

                }
            }
            binding.parentTxn.setOnClickListener {
                showTxn(item)
            }
        }
    }


}