package com.paulmerchants.gold.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paulmerchants.gold.R
import com.paulmerchants.gold.databinding.ItemLoansOverViewNewBinding
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import kotlin.math.absoluteValue

class GoldLoanOverViewAdapterProd(
    private val optionsClicked: (RespGetLoanOutStandingItem, Boolean) -> Unit,
    private val pyNowButtonClicked: (RespGetLoanOutStandingItem) -> Unit,
//    private val viewDetails: (RespGetLoanOutStandingItem) -> Unit,
) : ListAdapter<RespGetLoanOutStandingItem, GoldLoanOverViewAdapterProd.GoldLoanOverViewHolder>(
    DIFF_CALLBACK
) {

    var isShowCustomPay: Boolean? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GoldLoanOverViewHolder(
        ItemLoansOverViewNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GoldLoanOverViewHolder, position: Int) {
        holder.bindLast(getItem(position), optionsClicked, pyNowButtonClicked)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RespGetLoanOutStandingItem>() {
            override fun areItemsTheSame(
                oldItem: RespGetLoanOutStandingItem,
                newItem: RespGetLoanOutStandingItem,
            ): Boolean = true

            override fun areContentsTheSame(
                oldItem: RespGetLoanOutStandingItem,
                newItem: RespGetLoanOutStandingItem,
            ): Boolean = true
        }
    }

    inner class GoldLoanOverViewHolder(private val binding: ItemLoansOverViewNewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLast(
            actionItem: RespGetLoanOutStandingItem,
            optionsClicked: (RespGetLoanOutStandingItem, Boolean) -> Unit,
            pyNowButtonClicked: (RespGetLoanOutStandingItem) -> Unit,
        ) {
            var isSelected = false
            binding.apply {
                loanNumTv.text = actionItem.AcNo.toString()
                viewDetailsBtn.setOnClickListener {
//                    viewDetails(actionItem)
                }
                loanClosedBtn.setOnClickListener {
                    pyNowButtonClicked(actionItem)
                }
            }
            if (isShowCustomPay == true) {
                binding.clickPayParent.show()
            }
            binding.clickPayParent.setOnClickListener {
                if (!isSelected) {
                    binding.selectPrecard.setBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context, R.color.splash_screen_one
                        )
                    )
                    isSelected = true
                } else {
                    binding.selectPrecard.setBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context, R.color.white
                        )
                    )
                    isSelected = false
                }
                optionsClicked(actionItem, isSelected)
            }

            binding.apply {
                if (actionItem.closed != true) {
                    binding.apply {
//                        parentOpenLoan.show()
                        ovrDueParentArrow.show()
                        intDueAmountTitleTv.show()
                        intDueAmountTv.show()
//                        clickPayParent.show()

//                        outStaTitleTv.text =
//                            binding.root.context.getString(R.string.due_date)
//                        outStandValueTv.text =
//                            AppUtility.getDateFormat(actionItem.dueDate)?.trim().toString()
//                        val duedate = AppUtility.numberOfDaysWrtCurrent(actionItem.dueDate)
//                        val duedate = AppUtility.getCurrentDateOnly()
                        overDueDaysTv.text = "Due till date\n${actionItem.currentDate}"
//                        when {
//                            duedate.toInt() < 0 -> {
//                                Log.d("TAG", "bind: ----< than 0")
//                                ovrDueParentArrow.setBackgroundResource(R.drawable.rect_due_green)
//                                overDueDaysTv.text = "Due in ${duedate.absoluteValue} days"
//                            }
//
//                            else -> {
//                                Log.d("TAG", "bind: ----else ---- ")
//                                ovrDueParentArrow.setBackgroundResource(R.drawable.rectangle_due_red)
//                                overDueDaysTv.text = "Overdue by $duedate days"
//                            }
//                        }
//                        actionItem.InterestDue?.let {
//                            intDueAmountTv.text = "INR ${actionItem.OutStanding?.plus(it)}"
//                        }

                        intDueAmountTv.text =
                            if (actionItem.payableAmount != null) "INR ${actionItem.payableAmount}" else ""

                        binding.loanClosedBtn.apply {
                            text = binding.root.context.getString(R.string.pay_now)
                            setTextColor(
                                ContextCompat.getColor(
                                    binding.root.context, R.color.yellow_main
                                )
                            )
                        }
                    }
                } else {
                    Log.d("TAG", "bindLast: ...else.....")
                    binding.apply {
                        ovrDueParentArrow.hide()
                        intDueAmountTitleTv.hide()
                        intDueAmountTv.hide()
                        clickPayParent.hide()
                        outStaTitleTv.text = root.context.getString(R.string.final_due_paid)
                        if (actionItem.closedDate != "" && actionItem.closedDate != null) {

                            outStandValueTv.text =
                                AppUtility.getDateFormat(actionItem.closedDate.toString())
                            outStandValueTv.show()
                        } else {
                            outStandValueTv.hide()
                        }

                        binding.loanClosedBtn.apply {
                            text = binding.root.context.getString(R.string.loan_closed)
                            setTextColor(
                                ContextCompat.getColor(
                                    binding.root.context, R.color.white
                                )
                            )
                        }
                    }
                }
            }
        }


    }

    fun isShowSelctOption(isShow: Boolean) {
        isShowCustomPay = isShow
    }

//    fun setSelectedPosition(i: Int) {
//        isSelected = i
//    }

}