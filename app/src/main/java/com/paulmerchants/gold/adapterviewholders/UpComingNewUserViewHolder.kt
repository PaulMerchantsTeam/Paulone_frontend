package com.paulmerchants.gold.adapterviewholders

import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.databinding.NewUserServicesLayoutBinding
import com.paulmerchants.gold.model.OurServices

class UpComingNewUserViewHolder (private val binding: NewUserServicesLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(services: OurServices) {
        binding.apply {
            handIv.setImageResource(services.serviceImage)
            serviceTypeTv.text = services.serviceName

        }
//        binding.payNowBtn.setOnClickListener {
//            onPayDueClicked(dueLoans)
//        }
    }
}