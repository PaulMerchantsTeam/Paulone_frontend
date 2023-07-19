package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.common.PayMethod
import com.paulmerchants.gold.databinding.ItemPayModesSingleBinding
import com.paulmerchants.gold.model.PayModes
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show

class PaymentModeAdapter(
    private val pyNowButtonClicked: (PayModes) -> Unit,
) : ListAdapter<PayModes, PaymentModeAdapter.GoldLoanOverViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GoldLoanOverViewHolder(
        ItemPayModesSingleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GoldLoanOverViewHolder, position: Int) {
        holder.bindLast(getItem(position), pyNowButtonClicked)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PayModes>() {
            override fun areItemsTheSame(
                oldItem: PayModes,
                newItem: PayModes,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: PayModes,
                newItem: PayModes,
            ): Boolean = oldItem == newItem
        }
    }

    inner class GoldLoanOverViewHolder(private val binding: ItemPayModesSingleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLast(
            payModes: PayModes,
            pyNowButtonClicked: (PayModes) -> Unit,
        ) {
            if (payModes.id == PayMethod.BHIM.id) {
                binding.arrowGoIv.hide()
                binding.prepdCardTv.hide()
                binding.cardPcNumTv.hide()
                binding.addAnyTv.show()
            } else {
                binding.apply {
                    prepdCardTv.text = payModes.title
                    cardPcNumTv.text = payModes.numberCred
                    payImageOptTv.setImageResource(payModes.icon)
                    arrowGoIv.setOnClickListener {
                        pyNowButtonClicked(payModes)
                    }
                }
            }
        }
    }


}