package com.paulmerchants.gold.ui.bottom

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.GoldLoanOverViewAdapterProd
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.GoldLoanScreenFragmentBinding
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.CommonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoldLoanScreenFrag :
    BaseFragment<GoldLoanScreenFragmentBinding>(GoldLoanScreenFragmentBinding::inflate) {
    private var amount: Int = 0
    private val commonViewModel: CommonViewModel by viewModels()
    val lastStatementAdapter =
        GoldLoanOverViewAdapterProd(::optionsClicked, ::payNowClicked, ::viewDetails)

    override fun GoldLoanScreenFragmentBinding.initialize() {
        binding.headerBillMore.apply {
            backIv.hide()
            titlePageTv.text = getString(R.string.loan_overview)
            subTitle.hide()
        }
    }

    private fun optionsClicked(actionItem: RespGetLoanOutStandingItem, isSelect: Boolean) {
        Log.d("TAG", "optionsClicked: .............${actionItem.OutStanding}")
        actionItem.OutStanding?.let { totalAmount(it, isSelect) }
    }

    private fun totalAmount(rupees: Int, isSelect: Boolean) {
        if (isSelect) amount += rupees else amount -= rupees
        Log.d("TAG", "totalAmount: ........$amount")
        if (amount > 0) {
            binding.ttlAmountTv.show()
            binding.ttlAmountNumTv.show()
            binding.ttlAmountNumTv.text = "INR $amount"
        } else {
            binding.ttlAmountTv.hide()
            binding.ttlAmountNumTv.hide()
        }
    }

    private fun viewDetails(actionItem: RespGetLoanOutStandingItem) {
        if (actionItem.IsClosed == false) {
            val bundle = Bundle().apply {
                putParcelable(Constants.LOAN_OVERVIEW, actionItem)
            }
            findNavController().navigate(R.id.pmlGoldLoan, bundle)
        } else {
            val bundle = Bundle().apply {
                putParcelable(Constants.LOAN_OVERVIEW, actionItem)
            }
            findNavController().navigate(R.id.loanStatementFrag, bundle)
        }

    }

    private fun payNowClicked(actionItem: RespGetLoanOutStandingItem) {
        if (actionItem.IsClosed == false) {
            findNavController().navigate(R.id.paymentModesFrag)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.apply {
            goldLoanParentMain.openLoanTv.setOnClickListener {
                goldLoanParentMain.openLoanTv.setBackgroundResource(R.drawable.rec_sky_loan_blue_solid)
                goldLoanParentMain.closedLoanTv.setBackgroundColor(resources.getColor(R.color.splash_screen_two))
                setUiFoOpenGoldLoans()
                lastStatementAdapter.notifyDataSetChanged()
            }

            goldLoanParentMain.closedLoanTv.setOnClickListener {
                goldLoanParentMain.closedLoanTv.setBackgroundResource(R.drawable.rec_sky_loan_blue_solid)
                goldLoanParentMain.openLoanTv.setBackgroundColor(resources.getColor(R.color.splash_screen_two))
                setUiForClosedGoldLoans()
                lastStatementAdapter.notifyDataSetChanged()
            }
        }
        commonViewModel.getLoanOutstanding()
        commonViewModel.getRespGetLoanOutStandingLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.size != 0) {
                    if (it.size == 1) {
                        hideViews()
                    }
                    commonViewModel.respGetLoanOutStanding = it
                    setUiFoOpenGoldLoans()
                } else {
                    hideViews()
                }
            }
        }

        binding.payAllBtn.setOnClickListener {
            lastStatementAdapter.isShowSelctOption(true)
            lastStatementAdapter.notifyDataSetChanged()
        }
    }

    private fun hideViews() {
        binding.constraintLayout12.hide()
    }

    private fun setUiForClosedGoldLoans() {
//        binding.goldLoanParentMain.rvLoanOverViewMain.setGoldLoanOverView(0)
        val closed = commonViewModel.respGetLoanOutStanding.filter { it.IsClosed == true }
        Log.d("TAG", "setUiFoOpenGoldLoans: $closed")
        setData(closed as ArrayList<RespGetLoanOutStandingItem>)
    }

    private fun setUiFoOpenGoldLoans() {
//        binding.goldLoanParentMain.rvLoanOverViewMain.setGoldLoanOverView(1)
        val open = commonViewModel.respGetLoanOutStanding.filter { it.IsClosed == false }
        Log.d("TAG", "setUiFoOpenGoldLoans: $open")
        setData(open as ArrayList<RespGetLoanOutStandingItem>)

    }

    private fun setData(data: ArrayList<RespGetLoanOutStandingItem>) {
        lastStatementAdapter.submitList(null)
        lastStatementAdapter.submitList(data)
        binding.goldLoanParentMain.rvLoanOverViewMain.adapter = lastStatementAdapter
    }


    private fun setClickListener() {


    }

}