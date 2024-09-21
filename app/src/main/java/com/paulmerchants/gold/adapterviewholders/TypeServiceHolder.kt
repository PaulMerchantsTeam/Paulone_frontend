package com.paulmerchants.gold.adapterviewholders

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.ItemOptionMenuBinding
import com.paulmerchants.gold.model.TypeService

class TypeServiceHolder(private val binding: ItemOptionMenuBinding) :
    ViewHolder(binding.root) {



    fun bind(
        actionItem: TypeService,
        onTypeServiceClicked: (TypeService) -> Unit,
        position: Int?,
        isSelected: Int?
    ) {
        if (position == isSelected) {
            binding.serviceMenuParent.setBackgroundResource(R.drawable.rec_back_btn_41_empty)
        } else {
            binding.serviceMenuParent.setBackgroundResource(0)
        }
        binding.apply {
            binding.apply {
                serviceNameTv.text = actionItem.serviceName
            }
            binding.serviceMenuParent.setOnClickListener {
                onTypeServiceClicked(actionItem)
            }
        }
    }


}