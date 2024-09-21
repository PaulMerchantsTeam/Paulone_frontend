package com.paulmerchants.gold.adapterviewholders

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.ItemServiceMenuBinding
import com.paulmerchants.gold.model.MenuServices
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show

class MenuServicesHolder(private val binding: ItemServiceMenuBinding) :
    ViewHolder(binding.root) {

    fun bind(
        actionItem: MenuServices,
        onMenuServiceClicked: (MenuServices) -> Unit,
        onMenuServiceClickedTwo: (MenuServices) -> Unit,
        onMenuServiceTitleClicked: (MenuServices) -> Unit
    ) {
        if (actionItem.serviceId >= 100) {
            binding.titleServiceTv.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.splash_screen_three
                )
            )
        }
        if (actionItem.optOne == "" && actionItem.optTwo == "") {
            binding.apply {
                headerNext.show()
                view3.hide()

                serviceOne.hide()

            }
        }
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

                } else {
                    serviceTwo.show()

                    serviceTwo.text = actionItem.optTwo
                }
            }
        }

        binding.serviceOne.setOnClickListener {
            onMenuServiceClicked(actionItem)
        }

        binding.serviceTwo.setOnClickListener {
            onMenuServiceClickedTwo(actionItem)
        }

        binding.titleServiceTv.setOnClickListener {
            onMenuServiceTitleClicked(actionItem)
        }
    }
}