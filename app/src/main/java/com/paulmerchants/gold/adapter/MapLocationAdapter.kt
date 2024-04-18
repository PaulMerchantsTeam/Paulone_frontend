package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.ItemLocationAddBinding
import com.paulmerchants.gold.model.newmodel.PmlBranch

class MapLocationAdapter(
    private val onLocationClicked: (PmlBranch) -> Unit,
    private val onMarkLocation: (PmlBranch) -> Unit,
) : PagingDataAdapter<PmlBranch, MapLocationAdapter.MapLocationViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MapLocationViewHolder(
        ItemLocationAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MapLocationViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onLocationClicked, onMarkLocation) }
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<PmlBranch>() {
                override fun areItemsTheSame(
                    oldItem: PmlBranch,
                    newItem: PmlBranch,
                ): Boolean =
                    oldItem.branchId == newItem.branchId

                override fun areContentsTheSame(
                    oldItem: PmlBranch,
                    newItem: PmlBranch,
                ): Boolean =
                    oldItem == newItem
            }
    }

    inner class MapLocationViewHolder(private val binding: ItemLocationAddBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            pmlBranch: PmlBranch,
            onLocationClicked: (PmlBranch) -> Unit,
            onMarkLocation: (PmlBranch) -> Unit,
        ) {
            binding.apply {
                addressLocTv.text = pmlBranch.branchName
                distanceFromHereTv.text = binding.root.context.getString(R.string.check_location)
            }
            binding.checkLocation.setOnClickListener {
                onLocationClicked(pmlBranch)
            }
            binding.root.setOnClickListener {
                onMarkLocation(pmlBranch)
            }
        }
    }


}