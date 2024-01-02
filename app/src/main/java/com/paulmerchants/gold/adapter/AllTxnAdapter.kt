package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.ItemTxnLayoutBinding
import com.paulmerchants.gold.model.newmodel.Transactions
import com.paulmerchants.gold.utility.AppUtility

class AllTxnAdapter(private val showTxn: (Transactions) -> Unit) :
    PagingDataAdapter<Transactions, AllTxnAdapter.AllTxnAdapter>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AllTxnAdapter(
        ItemTxnLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: AllTxnAdapter, position: Int) {
        getItem(position)?.let { holder.bindLast(it,showTxn) }
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
                binding.transReferIdTv.text =
                    "Amount: ${binding.root.context.getString(R.string.Rs)} ${item.amount}\n" +
                            "Customer Id: ${item.custId}\n" +
                            "Transaction Id: ${item.paymentId?:""}\n" +
                            "Receipt Id:  ${item.receiptId}\n" +
                            "Date: ${AppUtility.formatDateFromMilliSec(item.createdAt)}"
                binding.statusTv.text = "Status: ${item.status}"
            }
            binding.parentTxn.setOnClickListener{
                showTxn(item)
            }
        }
    }


}

/**
 * "id": 12,
"orderId": "order_NDnK6PEmzcEpWq",
"amount": 1892,
"receiptId": "PaulOne_B7lmXKx921GxR5",
"status": "PAID",
"custId": "182005482096",
"paymentId": "pay_NDnKKJ9GUS3nlZ",
"createdBy": 1,
"updatedBy": 1,
"createdAt": 1702899512000,
"updatedAt": 1702899530449,
"processFlag": 1,
"requestId": "2ADD83E55C61467D8520231218170832161"
 */