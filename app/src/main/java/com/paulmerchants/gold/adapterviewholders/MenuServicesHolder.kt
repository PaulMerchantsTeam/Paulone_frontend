package com.paulmerchants.gold.adapterviewholders

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.databinding.ItemServiceMenuBinding
import com.paulmerchants.gold.model.MenuServices
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show

class MenuServicesHolder(private val binding: ItemServiceMenuBinding) :
    ViewHolder(binding.root) {

    fun bind(actionItem: MenuServices) {
        binding.apply {
            binding.apply {
                if (actionItem.titleName == "") {
                    binding.titleServiceTv.hide()
                } else {
                    titleServiceTv.text = actionItem.titleName
                    binding.titleServiceTv.show()
                }
                serviceOne.text = actionItem.optOne
                if (actionItem.optTwo == "") {
                    serviceTwo.hide()
                    option2Next.hide()
                } else {
                    serviceTwo.show()
                    option2Next.show()
                    serviceTwo.text = actionItem.optTwo
                }
            }
        }
    }
}