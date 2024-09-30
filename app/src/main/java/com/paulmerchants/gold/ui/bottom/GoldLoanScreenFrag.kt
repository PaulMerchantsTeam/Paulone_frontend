package com.paulmerchants.gold.ui.bottom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.GoldLoanOverViewAdapterProd
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.common.Constants.AMOUNT_PAYABLE
import com.paulmerchants.gold.databinding.GoldLoanScreenFragmentBinding
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem
import com.paulmerchants.gold.model.newmodel.PayAll
import com.paulmerchants.gold.model.newmodel.PayAllnOneGoDataTobeSent
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.ui.PaymentActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.getTwoDigitAfterDecimal
import com.paulmerchants.gold.utility.AppUtility.hideShim
import com.paulmerchants.gold.utility.AppUtility.showShimmer
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.GoldLoanScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GoldLoanScreenFrag :
    BaseFragment<GoldLoanScreenFragmentBinding>(GoldLoanScreenFragmentBinding::inflate) {
    private var amount: Int = 0
    private var listPayAll: ArrayList<PayAll> = arrayListOf()
    private val lastStatementAdapter =
        GoldLoanOverViewAdapterProd(::optionsClicked, ::payNowClicked)
    private val goldScreenViewModel: GoldLoanScreenViewModel by viewModels()

    override fun GoldLoanScreenFragmentBinding.initialize() {
        binding.headerBillMore.apply {
            backIv.setOnClickListener { findNavController().navigateUp() }
            titlePageTv.text = getString(R.string.loan_overview)
            subTitle.hide()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

    }
    private fun observeViewModel() {
        (activity as MainActivity).commonViewModel.isRemoteConfigCheck.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    (activity as MainActivity).showUnderMainTainPage()
                }
            }
        }
        (activity as MainActivity).commonViewModel.isUnderMainLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.statusCode == "200") {
                    if (it.data.down && it.data.id == 1) {
                        findNavController().navigate(R.id.mainScreenFrag)
                        (activity as MainActivity).binding.bottomNavigationView.hide()
                    } else if (it.data.down && it.data.id == 2) {
                        findNavController().navigate(R.id.loginScreenFrag)
                        (activity as MainActivity).binding.bottomNavigationView.hide()
                        (activity as MainActivity).binding.underMainTimerParent.root.show()
                    }
                    else if (!it.data.down){
//
                        (activity as MainActivity).binding.underMainTimerParent.root.hide()
                        setUpNetworkCallbackFOrDueLoans()
                    }
                    else {
                        setUpNetworkCallbackFOrDueLoans()
                    }
                }
            }
        }




        goldScreenViewModel.getRespGetLoanOutStandingLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.getLoanOutstandingResponseData.size != 0) {
                    if (it.getLoanOutstandingResponseData.size == 1) {
                        hideViews()
                    }
                    goldScreenViewModel.respGetLoanOutStanding =
                        it.getLoanOutstandingResponseData as ArrayList<RespGetLoanOutStandingItem>
                    for (i in it.getLoanOutstandingResponseData) {
                        i.currentDate = it.currentDate
                    }

                    setUiFoOpenGoldLoans()
                } else {
                    hideViews()
                }
            }
        }
    }

    private fun optionsClicked(actionItem: RespGetLoanOutStandingItem, isSelect: Boolean) {
        Log.d("TAG", "optionsClicked: .............${actionItem.interestDue}")
        actionItem.payableAmount?.let {
            totalAmount(
                actionItem,
                it.toInt(),
                isSelect
            )
        }
    }

    private fun totalAmount(
        actionItem: RespGetLoanOutStandingItem,
        rupees: Int,
        isSelect: Boolean,
    ) {
        if (isSelect) {
            amount += rupees
            if (actionItem.payableAmount != null) {
                listPayAll.add(
                    PayAll(
                        actionItem.AcNo.toString(),
                        actionItem.payableAmount
                    )
                )
            }

        } else {
            amount -= rupees
            if (actionItem.payableAmount != null) {
                listPayAll.remove(
                    PayAll(
                        actionItem.AcNo.toString(),
                        actionItem.payableAmount.let {
                            actionItem.payableAmount
                        }
                    )
                )
            }
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

//    private fun viewDetails(actionItem: RespGetLoanOutStandingItem) {
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

//    }

    private fun payNowClicked(actionItem: RespGetLoanOutStandingItem) {
        if (actionItem.closed == false) {
            val bundle = Bundle().apply {
                actionItem.payableAmount?.let {
                    putDouble(
                        AMOUNT_PAYABLE,
                        it
                    )

                }
                putString(Constants.CUST_ACC, actionItem.AcNo.toString())
            }
            goldScreenViewModel.isCalledGoldLoanScreen = true
//            val intent = Intent(requireContext(), PaymentActivity::class.java)
//            intent.putExtras(bundle)
//            startActivity(intent)
            findNavController().navigate(R.id.paymentModesFragNew, bundle)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "onStop: ......")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG", "onDestroy: /////")
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).commonViewModel.getUnderMaintenanceStatus()
        (activity as MainActivity).checkForDownFromRemoteConfig()

        if (goldScreenViewModel.isCalledGoldLoanScreen) {
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
                    goldLoanParentMain.closedLoanTv.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.splash_screen_two
                        )
                    )
                    setUiFoOpenGoldLoans()
                    lastStatementAdapter.notifyDataSetChanged()
                    binding.constraintLayout12.hide()
                    binding.ttlAmountNumTv.text = "INR 0"
                }

                goldLoanParentMain.closedLoanTv.setOnClickListener {
                    goldLoanParentMain.closedLoanTv.setBackgroundResource(R.drawable.rec_sky_loan_blue_solid)
                    goldLoanParentMain.openLoanTv.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.splash_screen_two
                        )
                    )
                    setUiForClosedGoldLoans()
                    lastStatementAdapter.notifyDataSetChanged()
                    amount = 0
                    binding.constraintLayout12.hide()
                    binding.ttlAmountNumTv.text = "INR 0"
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
                    goldScreenViewModel.isCalledGoldLoanScreen = true
//                    "val intent = Intent(requireContext(), PaymentActivity::class.java)
//                    intent.putExtras(bundle)
//                    startActivity(intent)"
                    findNavController().navigate(R.id.paymentModesFragNew, bundle)
                } else {
                    lastStatementAdapter.isShowSelctOption(true)
                    lastStatementAdapter.notifyDataSetChanged()
                }

            }
        } else {

        }
    }

    private fun setUpNetworkCallbackFOrDueLoans() {
        goldScreenViewModel.getLoanOutstanding((activity as MainActivity).mLocation)
    }

    private fun hideViews() {
        binding.constraintLayout12.hide()
    }

    override fun onPause() {
        super.onPause()
        goldScreenViewModel.isCalledGoldLoanScreen = false
    }

    private fun setUiForClosedGoldLoans() {
//        binding.goldLoanParentMain.rvLoanOverViewMain.setGoldLoanOverView(0)
        val closed = goldScreenViewModel.respGetLoanOutStanding.filter { it.closed == true }
        Log.d("TAG", "setUiFoOpenGoldLoans: $closed")
        setData(closed as ArrayList<RespGetLoanOutStandingItem>)
    }

    private fun setUiFoOpenGoldLoans() {
//      binding.goldLoanParentMain.rvLoanOverViewMain.setGoldLoanOverView(1)
        var totalAmount = 0.0
        val open = goldScreenViewModel.respGetLoanOutStanding.filter { it.closed == false }
        Log.d("TAG", "setUiFoOpenGoldLoans: $open")

        for (i in open) {
            if (i.payableAmount != null) {
                totalAmount += getTwoDigitAfterDecimal(i.payableAmount).toFloat()
            }
        }

        when {
            open.size == 1 -> {
                AppUtility.diffColorText(
                    "You have ",
                    "${open.size}",
                    " active loan, and the interest due is up to ",
                    "INR ${getTwoDigitAfterDecimal(totalAmount).toFloat()}", "", "", binding.goldLoanParentMain.lonOverDesc
                )
            }

            open.size > 1 -> {
                AppUtility.diffColorText(
                    "You have ",
                    "${open.size}",
                    " active loans, and their total interest due is up to ",
                    "INR ${getTwoDigitAfterDecimal(totalAmount).toFloat()}", "", "", binding.goldLoanParentMain.lonOverDesc
                )
            }

            else -> {

            }
        }

        val notZeroInterestData = open.filter { it.payableAmount != 0.0 }
        if (notZeroInterestData.isNotEmpty()) {
            binding.constraintLayout12.hide()
            setData(notZeroInterestData as ArrayList<RespGetLoanOutStandingItem>)
        } else {
            binding.constraintLayout12.hide()
            "No Pending Due".showSnackBar()
        }

    }

    private fun setData(data: ArrayList<RespGetLoanOutStandingItem>) {
        lastStatementAdapter.submitList(null)
        lastStatementAdapter.submitList(data)
        binding.goldLoanParentMain.rvLoanOverViewMain.adapter = lastStatementAdapter
    }


    private fun setClickListener() {


    }

}