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
import com.paulmerchants.gold.databinding.GoldLoanScreenFragmentBinding
import com.paulmerchants.gold.model.responsemodels.RespGetLoanOutStandingItem
import com.paulmerchants.gold.model.other.PayAll
import com.paulmerchants.gold.model.other.PayAllnOneGoDataTobeSent
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.ui.PaymentActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.getTwoDigitAfterDecimal
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.AMOUNT_PAYABLE
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
                if (it.status_code ==200) {
                    if (it.data?.down == true && it.data.id == 1) {
                        findNavController().navigate(R.id.mainScreenFrag)
                        (activity as MainActivity).binding.bottomNavigationView.hide()
                    } else if (it.data?.down == true && it.data.id == 2) {
                        findNavController().navigate(R.id.loginScreenFrag)
                        (activity as MainActivity).binding.bottomNavigationView.hide()
                        (activity as MainActivity).binding.underMainTimerParent.root.show()
                    }
                    else if (it.data?.down == false){
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
            when (it.status_code) {
                200 -> {
                    it.data?.let { it ->
                        if (it.get_loan_outstanding_response_data.isNotEmpty()) {
                            if (it.get_loan_outstanding_response_data.size == 1) {
                                hideViews()
                            }
                            goldScreenViewModel.respGetLoanOutStanding =
                                it.get_loan_outstanding_response_data as ArrayList<RespGetLoanOutStandingItem>
                            for (i in it.get_loan_outstanding_response_data) {
                                i.current_date = it.current_date
                            }

                            setUiFoOpenGoldLoans()
                        } else {
                            hideViews()
                        }
                    }
                }
                498 -> {
                    (activity as MainActivity).commonViewModel.refreshToken(requireContext())
                }
                else -> {
                    it.message.showSnackBar()
                }
            }

        }
        (activity as MainActivity).commonViewModel.refreshTokenLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.status_code == 200) {
                    setUpNetworkCallbackFOrDueLoans()
                }
            }
        }
    }

    private fun optionsClicked(actionItem: RespGetLoanOutStandingItem, isSelect: Boolean) {
        Log.d("TAG", "optionsClicked: .............${actionItem.interest_due}")
        actionItem.payable_amount?.let {
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
            if (actionItem.payable_amount != null) {
                listPayAll.add(
                    PayAll(
                        actionItem.ac_no.toString(),
                        actionItem.payable_amount
                    )
                )
            }

        } else {
            amount -= rupees
            if (actionItem.payable_amount != null) {
                listPayAll.remove(
                    PayAll(
                        actionItem.ac_no.toString(),
                        actionItem.payable_amount.let {
                            actionItem.payable_amount
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



    private fun payNowClicked(actionItem: RespGetLoanOutStandingItem) {
        if (actionItem.is_closed == false) {
            val bundle = Bundle().apply {
                actionItem.payable_amount?.let {
                    putDouble(
                        AMOUNT_PAYABLE,
                        it
                    )

                }
                putString(Constants.CUST_ACC, actionItem.ac_no.toString())

            }
            goldScreenViewModel.isCalledGoldLoanScreen = true
            val intent = Intent(requireContext(), PaymentActivity ::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

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
        (activity as MainActivity).commonViewModel.getUnderMaintenanceStatus(requireContext())
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

                    }
                    goldScreenViewModel.isCalledGoldLoanScreen = true

                    findNavController().navigate(R.id.paymentModesFragNew, bundle)
                } else {
                    lastStatementAdapter.isShowSelctOption(true)
                    lastStatementAdapter.notifyDataSetChanged()
                }

            }
        } else {
//
        }
    }

    private fun setUpNetworkCallbackFOrDueLoans() {
        goldScreenViewModel.getLoanOutstanding((activity as MainActivity).mLocation,requireContext())
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
        val closed = goldScreenViewModel.respGetLoanOutStanding.filter { it.is_closed == true }
        Log.d("TAG", "setUiFoOpenGoldLoans: $closed")
        setData(closed as ArrayList<RespGetLoanOutStandingItem>)
    }

    private fun setUiFoOpenGoldLoans() {
//      binding.goldLoanParentMain.rvLoanOverViewMain.setGoldLoanOverView(1)
        var totalAmount = 0.0
        val open = goldScreenViewModel.respGetLoanOutStanding.filter { it.is_closed == false }
        Log.d("TAG", "setUiFoOpenGoldLoans: $open")

        for (i in open) {
            if (i.payable_amount != null) {
                totalAmount += getTwoDigitAfterDecimal(i.payable_amount).toFloat()
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

        val notZeroInterestData = open.filter { it.payable_amount != 0.0 }
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




}