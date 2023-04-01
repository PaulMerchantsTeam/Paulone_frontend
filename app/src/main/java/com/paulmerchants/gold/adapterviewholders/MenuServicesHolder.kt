package com.paulmerchants.gold.adapterviewholders

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.databinding.ItemServiceMenuBinding
import com.paulmerchants.gold.model.MenuServices

class MenuServicesHolder(private val binding: ItemServiceMenuBinding) :
    ViewHolder(binding.root) {

    fun bind(actionItem: MenuServices) {
        binding.apply {
            binding.apply {
                titleServiceTv.text = actionItem.titleName
                serviceOne.text = actionItem.optOne
                serviceTwo.text = actionItem.optTwo
            }
        }
    }
}