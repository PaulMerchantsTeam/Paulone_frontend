package com.paulmerchants.gold.adapterviewholders

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.databinding.NewUserServicesLayoutBinding
import com.paulmerchants.gold.model.OurServices

class UpComingNewUserViewHolder (private val binding: NewUserServicesLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(services: OurServices,pos:Int) {
        binding.apply {
            handIv.setImageResource(services.serviceImage)
            serviceTypeTv.text = services.serviceName
            serviceTypeTv.setTextColor(ContextCompat.getColor(
                binding.root.context,services.color
            ))
            when(pos){
                0 ->{}
                1 -> {}
                2 -> {}
                3-> {
                    digitalGoldIv.visibility = View.VISIBLE

                }
            }


        }
//        binding.payNowBtn.setOnClickListener {
//            onPayDueClicked(dueLoans)
//        }
    }
}