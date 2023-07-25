package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.paulmerchants.gold.adapterviewholders.TransDoneHolder
import com.paulmerchants.gold.databinding.ItemTransactionServicesBinding
import com.paulmerchants.gold.model.TransDoneModel

class TransacDoneAdapter(private val onTransactionClicked: (TransDoneModel) -> Unit) :
    ListAdapter<TransDoneModel, TransDoneHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TransDoneHolder(
        ItemTransactionServicesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TransDoneHolder, position: Int) {
        holder.bind(getItem(position),onTransactionClicked)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<TransDoneModel>() {
                override fun areItemsTheSame(
                    oldItem: TransDoneModel,
                    newItem: TransDoneModel,
                ): Boolean =
                    oldItem.transId == newItem.transId

                override fun areContentsTheSame(
                    oldItem: TransDoneModel,
                    newItem: TransDoneModel,
                ): Boolean =
                    oldItem == newItem
            }
    }


}