package com.paulmerchants.gold.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.paulmerchants.gold.adapterviewholders.UpComingNewUserViewHolder
import com.paulmerchants.gold.databinding.NewUserServicesLayoutBinding
import com.paulmerchants.gold.model.OurServices

class UpcomingLoanNewuserAdapter() :
ListAdapter<OurServices, UpComingNewUserViewHolder>(DIFF_CALLBACK)  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UpComingNewUserViewHolder(
        NewUserServicesLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: UpComingNewUserViewHolder, position: Int) {
        holder.bind(getItem(position))

    }
    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<OurServices>() {
                override fun areItemsTheSame(
                    oldItem: OurServices,
                    newItem: OurServices,
                ): Boolean =
                    oldItem.serviceImage == newItem.serviceImage

                override fun areContentsTheSame(
                    oldItem: OurServices,
                    newItem: OurServices,
                ): Boolean =
                    oldItem == newItem
            }
    }
}