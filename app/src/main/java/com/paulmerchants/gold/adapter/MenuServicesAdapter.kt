package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.paulmerchants.gold.adapterviewholders.MenuServicesHolder
import com.paulmerchants.gold.databinding.ItemServiceMenuBinding
import com.paulmerchants.gold.model.other.MenuServices

class MenuServicesAdapter(
    private val onMenuServiceClicked: (MenuServices) -> Unit,
    private val onMenuServiceClickedTwo: (MenuServices) -> Unit,
    private val onMenuServiceTitleCLicked: (MenuServices) -> Unit
) :
    ListAdapter<MenuServices, MenuServicesHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MenuServicesHolder(
        ItemServiceMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MenuServicesHolder, position: Int) {
        holder.bind(
            getItem(position),
            onMenuServiceClicked,
            onMenuServiceClickedTwo,
            onMenuServiceTitleCLicked
        )
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<MenuServices>() {
                override fun areItemsTheSame(
                    oldItem: MenuServices,
                    newItem: MenuServices,
                ): Boolean =
                    oldItem.serviceId == newItem.serviceId

                override fun areContentsTheSame(
                    oldItem: MenuServices,
                    newItem: MenuServices,
                ): Boolean =
                    oldItem == newItem
            }
    }


}