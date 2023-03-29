package com.paulmerchants.gold.ui.bottom

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.HomeSweetBillsAdapter
import com.paulmerchants.gold.adapter.UpcomingLoanAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.DummyHomeScreenFragmentBinding
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.model.DueLoans
import com.paulmerchants.gold.utility.startCustomAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay


@AndroidEntryPoint
class HomeScreenFrag :
    BaseFragment<DummyHomeScreenFragmentBinding>(DummyHomeScreenFragmentBinding::inflate) {
    private val upcomingLoanAdapter = UpcomingLoanAdapter()
    private val homeSweetBillsAdapter = HomeSweetBillsAdapter()
    private val TAG = "HomeScreenFrag"

    //    private var isStartAnim = true
    private var isStartAnim = MutableLiveData<Boolean>()
    override fun DummyHomeScreenFragmentBinding.initialize() {
        setUpComingDueLoans()
    }

    override fun onStart() {
        super.onStart()
        setProfileUi()
        startAnimationOnIcon()
        setUiOnHomeSweetHomeBills()
    }

    private fun setProfileUi() {
        isStartAnim.postValue(true)
        isStartAnim.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    animateHintEditText()
                }
            }
        }

        binding.searchProfileParent.apply {
            searchView.setOnClickListener {
                isStartAnim.postValue(false)
            }

            searchView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    isStartAnim.postValue(false)
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    isStartAnim.postValue(false)
                    p0?.let { char ->
                        Log.d("TAG", "onTextChanged: text = $char")
                        if (char.isNotEmpty()) {
                            isStartAnim.postValue(false)
//                            searchView.setCompoundDrawablesWithIntrinsicBounds(
//                                0,
//                                0,
//                                0,
//                                0
//                            )
                        } else {
                            isStartAnim.postValue(false)
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    Log.d("TAG", "afterTextChanged: $p0")
                }
            })
        }

    }

    private fun handleRechargeAndBillUi() {
        /**
         * initially hide ---home_sweet_home, financial_Security,transit_window ,back_top
         * |-----flow----|
         * on ... more click
         * hide own parent icon  and change behaviour too
         * show---home_sweet_home, financial_Security,transit_window ,back_top
         *
         */
        binding.allPaymnetActionParent.apply {
            moreParent.setOnClickListener {

            }
        }

    }

    private fun animateHintEditText() {
        val strList = listOf(
            getString(R.string.search_fr_bills),
            getString(R.string.search_fr_credit),
            getString(R.string.search_fr_upcoming_dues),
            getString(R.string.search_for_loans),

            )
        lifecycleScope.launchWhenResumed {
            delay(2000)
            binding.searchProfileParent.searchView.hint = strList[0]
            delay(2000)
            binding.searchProfileParent.searchView.hint = strList[1]
            delay(2000)
            binding.searchProfileParent.searchView.hint = strList[2]
            delay(2000)
            binding.searchProfileParent.searchView.hint = strList[3]
            delay(5000)
            isStartAnim.postValue(false)
        }

    }

    private fun startAnimationOnIcon() {
        binding.allPaymnetActionParent.apply {
            goldIv.startCustomAnimation(R.drawable.gold_icon_anim)
            dthIV.startCustomAnimation(R.drawable.dth_service_icon_anim)
            elecIv.startCustomAnimation(R.drawable.elec_icon_anim)
            boradBandIv.startCustomAnimation(R.drawable.broadband_icon_anim)
            mobileIv.startCustomAnimation(R.drawable.mobile_icon_anim)
            creditCardIv.startCustomAnimation(R.drawable.credit_icon_anim)
        }
    }

    private fun setUpComingDueLoans() {
        val dueLoans1 = DueLoans(1, 4, 6000)
        val dueLoans2 = DueLoans(2, 4, 6000)
        val dueLoans3 = DueLoans(3, 4, 6000)
        val dueLoans4 = DueLoans(4, 4, 6000)
        val list = listOf(dueLoans1, dueLoans2, dueLoans3, dueLoans4)
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
        val list = listOf(
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