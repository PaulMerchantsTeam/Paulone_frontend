package com.paulmerchants.gold.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.ItemLoansOverViewBinding
import com.paulmerchants.gold.databinding.ItemPrepaidCardWithNameBinding
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show

class PrepaidMainAdapter(private val viewBtnClicked: (ActionItem, Boolean) -> Unit) :
    ListAdapter<ActionItem, PrepaidMainAdapter.GoldLoanOverViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GoldLoanOverViewHolder(
        ItemPrepaidCardWithNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GoldLoanOverViewHolder, position: Int) {
        holder.bindLast(getItem(position), viewBtnClicked)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<ActionItem>() {
                override fun areItemsTheSame(
                    oldItem: ActionItem,
                    newItem: ActionItem,
                ): Boolean =
                    oldItem.itemId == newItem.itemId

                override fun areContentsTheSame(
                    oldItem: ActionItem,
                    newItem: ActionItem,
                ): Boolean =
                    oldItem == newItem
            }
    }

    inner class GoldLoanOverViewHolder(private val binding: ItemPrepaidCardWithNameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLast(actionItem: ActionItem, buttonClicked: (ActionItem, Boolean) -> Unit) {
            var isShowCard = false
            binding.titleNameCardTv.text = actionItem.name
            binding.viewCardBtn.setOnClickListener {
                if (!isShowCard) {
                    binding.apply {
                        parentCardInfo.setBackgroundResource(R.drawable.rec_frch_blue_solid)
                        expTv.show()
                        cvvTv.show()
                        getDetailsBtn.show()
//                            otpDescTv.show()
                        viewCardBtn.text = ""
                        viewCardBtn.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.arrow_up_black,
                            0
                        )

                    }
                    isShowCard = true
                } else {
                    binding.apply {
                        parentCardInfo.setBackgroundResource(R.drawable.prepaid_card_back_half)
                        expTv.hide()
                        cvvTv.hide()
                        getDetailsBtn.hide()
//                            otpDescTv.hide()
                        viewCardBtn.text = binding.root.context.getString(R.string.view_card)
                        viewCardBtn.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            0
                        )

                    }
                    isShowCard = false
                }
                buttonClicked(actionItem, isShowCard)
            }
        }

    }
}