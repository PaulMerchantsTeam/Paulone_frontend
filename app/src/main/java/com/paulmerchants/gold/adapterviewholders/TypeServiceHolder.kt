package com.paulmerchants.gold.adapterviewholders

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.databinding.ItemActionBillBinding
import com.paulmerchants.gold.databinding.ItemOptionMenuBinding
import com.paulmerchants.gold.databinding.ItemServiceMenuBinding
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.model.TypeService

class TypeServiceHolder(private val binding: ItemOptionMenuBinding) :
    ViewHolder(binding.root) {

    fun bind(actionItem: TypeService) {
        binding.apply {
            binding.apply {
                serviceNameTv.text = actionItem.serviceName
            }
        }
    }
}