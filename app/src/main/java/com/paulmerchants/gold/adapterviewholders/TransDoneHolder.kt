package com.paulmerchants.gold.adapterviewholders

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.databinding.ItemServiceMenuBinding
import com.paulmerchants.gold.databinding.ItemTransactionServicesBinding
import com.paulmerchants.gold.model.MenuServices
import com.paulmerchants.gold.model.TransDoneModel
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show

class TransDoneHolder(private val binding: ItemTransactionServicesBinding) :
    ViewHolder(binding.root) {

    fun bind(actionItem: TransDoneModel) {
        binding.apply {
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
                    amountTransacTv.text = actionItem.amountDone
                }else{
                    parentTransService.hide()
                }
            }
        }
    }
}
