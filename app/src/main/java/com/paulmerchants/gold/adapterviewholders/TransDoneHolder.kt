package com.paulmerchants.gold.adapterviewholders

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.databinding.ItemTransactionServicesBinding
import com.paulmerchants.gold.model.TransDoneModel
import com.paulmerchants.gold.utility.hide

class TransDoneHolder(private val binding: ItemTransactionServicesBinding) :
    ViewHolder(binding.root) {

    fun bind(actionItem: TransDoneModel, onTransactionClicked: (TransDoneModel) -> Unit) {

        binding.apply {
            if (actionItem.transImage != null) {
                transacIv.setImageResource(actionItem.transImage)
            }
            if (actionItem.transTitle != null) {
                transacTitalTv.text = actionItem.transTitle
            }
            if (actionItem.dateDone != null) {
                transacDateAndTimeTv.text = actionItem.dateDone

            }
            if (actionItem.amountDone != null) {
                amountTransacTv.text = "Rs ${actionItem.amountDone}"
            } else {
                parentTransService.hide()
            }
            parentTransService.setOnClickListener {
                onTransactionClicked(actionItem)
            }
        }
    }
}
