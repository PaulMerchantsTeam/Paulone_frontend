package com.paulmerchants.gold.ui.bottom

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.GoldLoanOverViewAdapterProd
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.common.Constants.IS_FROM_ALL_IN_ONE_GO
import com.paulmerchants.gold.databinding.GoldLoanScreenFragmentBinding
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem
import com.paulmerchants.gold.model.newmodel.PayAll
import com.paulmerchants.gold.model.newmodel.PayAllnOneGoDataTobeSent
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.CommonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GoldLoanScreenFrag :
    BaseFragment<GoldLoanScreenFragmentBinding>(GoldLoanScreenFragmentBinding::inflate) {
    private var amount: Int = 0
    private val commonViewModel: CommonViewModel by viewModels()
    private var listPayAll: ArrayList<PayAll> = arrayListOf()
    private val lastStatementAdapter =
        GoldLoanOverViewAdapterProd(::optionsClicked, ::payNowClicked, ::viewDetails)

    override fun GoldLoanScreenFragmentBinding.initialize() {
        binding.headerBillMore.apply {
            backIv.setOnClickListener { findNavController().navigateUp() }
            titlePageTv.text = getString(R.string.loan_overview)
            subTitle.hide()
        }
    }

    private fun optionsClicked(actionItem: RespGetLoanOutStandingItem, isSelect: Boolean) {
        Log.d("TAG", "optionsClicked: .............${actionItem.OutStanding}")
        actionItem.OutStanding?.let { totalAmount(actionItem, it, isSelect) }
    }

    private fun totalAmount(
        actionItem: RespGetLoanOutStandingItem,
        rupees: Int,
        isSelect: Boolean,
    ) {
        if (isSelect) {
            amount += rupees
            listPayAll.add(PayAll(actionItem.AcNo.toString(), actionItem.OutStanding?.toDouble()))
        } else {
            amount -= rupees
            listPayAll.remove(
                PayAll(
                    actionItem.AcNo.toString(),
                    actionItem.OutStanding?.toDouble()
                )
            )
        }
        Log.d("TAG", "totalAmount: ........$amount")
        if (amount > 0) {
            binding.ttlAmountTv.show()
            binding.ttlAmountNumTv.show()
            binding.ttlAmountNumTv.text = "INR $amount"
        } else {
            binding.ttlAmountTv.hide()
            binding.ttlAmountNumTv.hide()
        }
        Log.d("TAG", "totalAmount: ....................listPayAll-----------$listPayAll")
    }

    private fun viewDetails(actionItem: RespGetLoanOutStandingItem) {
//        if (actionItem.IsClosed == false) {
//            val bundle = Bundle().apply {
//                putParcelable(Constants.LOAN_OVERVIEW, actionItem)
//            }
//            findNavController().navigate(R.id.pmlGoldLoan, bundle)
//        } else {
//            val bundle = Bundle().apply {
//                putParcelable(Constants.LOAN_OVERVIEW, actionItem)
//            }
//            findNavController().navigate(R.id.loanStatementFrag, bundle)
//        }

    }

    private fun payNowClicked(actionItem: RespGetLoanOutStandingItem) {
        if (actionItem.IsClosed == false) {
            val bundle = Bundle().apply {
//                actionItem.OutStanding?.toDouble()?.let {
//                    putDouble(
//                        "AMOUNT_PAYABLE",
//                        it
//                    )
//                }
//                putString(Constants.CUST_ACC, actionItem.AcNo.toString())
//                putBoolean(IS_FROM_ALL_IN_ONE_GO, true)
                putParcelable(
                    Constants.PAY_ALL_IN_GO_DATA, PayAllnOneGoDataTobeSent(
                        amount.toDouble(),
                        listPayAll, true
                    )
                )
            }
            findNavController().navigate(R.id.paymentModesFragNew, bundle)
//            findNavController().navigate(R.id.paymentModesFrag)
        }
    }

    override fun onStart() {
        super.onStart()
        amount = 0
        lifecycleScope.launch(Dispatchers.Main) {
            binding.shmrLaonOverView.startShimmer()
            delay(1000)
            binding.shmrLaonOverView.stopShimmer()
            binding.shmrLaonOverView.hide()
            binding.goldLoanParentMain.rvLoanOverViewMain.show()
        }
        binding.apply {
            goldLoanParentMain.openLoanTv.setOnClickListener {
                goldLoanParentMain.openLoanTv.setBackgroundResource(R.drawable.rec_sky_loan_blue_solid)
                goldLoanParentMain.closedLoanTv.setBackgroundColor(resources.getColor(R.color.splash_screen_two))
                setUiFoOpenGoldLoans()
                lastStatementAdapter.notifyDataSetChanged()
                binding.constraintLayout12.show()
                binding.ttlAmountNumTv.text = "INR 0"
            }

            goldLoanParentMain.closedLoanTv.setOnClickListener {
                goldLoanParentMain.closedLoanTv.setBackgroundResource(R.drawable.rec_sky_loan_blue_solid)
                goldLoanParentMain.openLoanTv.setBackgroundColor(resources.getColor(R.color.splash_screen_two))
                setUiForClosedGoldLoans()
                lastStatementAdapter.notifyDataSetChanged()
                amount = 0
                binding.constraintLayout12.hide()
                binding.ttlAmountNumTv.text = "INR 0"
            }
        }
        commonViewModel.getLoanOutstanding((activity as MainActivity).appSharedPref)
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
            if (amount > 0) {
                val bundle = Bundle().apply {
                    putParcelable(
                        Constants.PAY_ALL_IN_GO_DATA, PayAllnOneGoDataTobeSent(
                            amount.toDouble(),
                            listPayAll, true
                        )
                    )
//                    putDouble("AMOUNT_PAYABLE", amount.toDouble())
//                    putString(Constants.CUST_ACC, "182222222222222")
//                    putBoolean(IS_FROM_ALL_IN_ONE_GO, true)
                }
                findNavController().navigate(R.id.paymentModesFragNew, bundle)
            } else {
                lastStatementAdapter.isShowSelctOption(true)
                lastStatementAdapter.notifyDataSetChanged()
            }

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
        var totalAmount = 0
        val open = commonViewModel.respGetLoanOutStanding.filter { it.IsClosed == false }
        Log.d("TAG", "setUiFoOpenGoldLoans: $open")

        for (i in open) {
            if (i.OutStanding != null) {
                totalAmount += i.OutStanding
            }
        }
        AppUtility.diffColorText(
            "You have taken up ",
            "${open.size}",
            " active loans. And they total upto ",
            "INR $totalAmount", "", "", binding.goldLoanParentMain.lonOverDesc
        )

        "You have taken up ${open.size} active loans. And they total upto INR $totalAmount"
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