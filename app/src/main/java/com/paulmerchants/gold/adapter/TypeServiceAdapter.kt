package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.paulmerchants.gold.adapterviewholders.TypeServiceHolder
import com.paulmerchants.gold.databinding.ItemOptionMenuBinding
import com.paulmerchants.gold.model.TypeService

class TypeServiceAdapter :
    ListAdapter<TypeService, TypeServiceHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TypeServiceHolder(
        ItemOptionMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TypeServiceHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<TypeService>() {
                override fun areItemsTheSame(
                    oldItem: TypeService,
                    newItem: TypeService,
                ): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: TypeService,
                    newItem: TypeService,
                ): Boolean =
                    oldItem == newItem
            }
    }


}