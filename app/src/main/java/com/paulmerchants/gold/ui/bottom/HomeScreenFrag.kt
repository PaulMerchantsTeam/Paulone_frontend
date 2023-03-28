package com.paulmerchants.gold.ui.bottom

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.HomeSweetBillsAdapter
import com.paulmerchants.gold.adapter.UpcomingLoanAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.HomeScreenFragmentBinding
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.model.DueLoans
import com.paulmerchants.gold.utility.startCustomAnimation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreenFrag : BaseFragment<HomeScreenFragmentBinding>(HomeScreenFragmentBinding::inflate) {
    private val upcomingLoanAdapter = UpcomingLoanAdapter()
    private val homeSweetBillsAdapter = HomeSweetBillsAdapter()

    override fun HomeScreenFragmentBinding.initialize() {
        setUpComingDueLoans()
    }

    override fun onStart() {
        super.onStart()
        startAnimationOnIcon()
        setUiOnHomeSweetHomeBills()
    }

    private fun startAnimationOnIcon() {
        binding.allPaymnetActionParent.goldIv.startCustomAnimation(R.drawable.gold_icon_anim)
        binding.allPaymnetActionParent.dthIV.startCustomAnimation(R.drawable.dth_service_icon_anim)
        binding.allPaymnetActionParent.elecIv.startCustomAnimation(R.drawable.elec_icon_anim)
        binding.allPaymnetActionParent.boradBandIv.startCustomAnimation(R.drawable.broadband_icon_anim)
        binding.allPaymnetActionParent.mobileIv.startCustomAnimation(R.drawable.mobile_icon_anim)
    }

    private fun setUpComingDueLoans() {
        val dueLoans1 = DueLoans(1, 4, 6000)
        val dueLoans2 = DueLoans(2, 4, 6000)
        val dueLoans3 = DueLoans(3, 4, 6000)
        val dueLoans4 = DueLoans(4, 4, 6000)
        val list = listOf<DueLoans>(dueLoans1, dueLoans2, dueLoans3, dueLoans4)
        upcomingLoanAdapter.submitList(list)
        binding.rvUpcomingDueLoans.adapter = upcomingLoanAdapter
    }

    private fun setUiOnHomeSweetHomeBills() {
        val actionItem1 = ActionItem(1, R.drawable.elec_bill, getString(R.string.electricity))
        val actionItem2 = ActionItem(2, R.drawable.broadband_bill, getString(R.string.broadband))
        val actionItem3 = ActionItem(3, R.drawable.education_loan, getString(R.string.education))
        val actionItem4 = ActionItem(4, R.drawable.cylinder_gas, getString(R.string.gas_cylinder))
        val actionItem5 = ActionItem(5, R.drawable.apartment_group, getString(R.string.apartment))
        val actionItem6 = ActionItem(6, R.drawable.pipeline_gas, getString(R.string.gas_pipline))
        val actionItem7 = ActionItem(7, R.drawable.home_rent, getString(R.string.homerent))
        val actionItem8 = ActionItem(8, R.drawable.tap, getString(R.string.water))
        val actionItem9 = ActionItem(9, R.drawable.landline_action, getString(R.string.landline))
        val actionItem10 = ActionItem(10, R.drawable.cable_tv, getString(R.string.cabletv))
        val list = listOf<ActionItem>(
            actionItem1,
            actionItem2,
            actionItem3,
            actionItem4,
            actionItem5,
            actionItem6,
            actionItem7,
            actionItem8,
            actionItem9,
            actionItem10
        )
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL)
        homeSweetBillsAdapter.submitList(list)
        binding.allPaymnetActionParent.homeSweetHomBillsRv.apply {
            layoutManager = staggeredGridLayoutManager
            adapter = homeSweetBillsAdapter
        }
    }


}