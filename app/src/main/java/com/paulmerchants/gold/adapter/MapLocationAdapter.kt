package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.databinding.ItemLocationAddBinding
import com.paulmerchants.gold.place.Place

class MapLocationAdapter(
    private val OnLocationClicked: (Place) -> Unit,
) :
    ListAdapter<Place, MapLocationAdapter.MapLocationViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MapLocationViewHolder(
        ItemLocationAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MapLocationViewHolder, position: Int) {
        holder.bind(getItem(position), OnLocationClicked)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Place>() {
                override fun areItemsTheSame(
                    oldItem: Place,
                    newItem: Place,
                ): Boolean =
                    oldItem.name == newItem.name

                override fun areContentsTheSame(
                    oldItem: Place,
                    newItem: Place,
                ): Boolean =
                    oldItem == newItem
            }
    }

    inner class MapLocationViewHolder(private val binding: ItemLocationAddBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(place: Place, OnLocationClicked: (Place) -> Unit) {
            binding.apply {
                addressLocTv.text = place.name
                distanceFromHereTv.text = "3 Kms"
            }
            binding.root.setOnClickListener {
                OnLocationClicked(place)
            }
        }
    }


}