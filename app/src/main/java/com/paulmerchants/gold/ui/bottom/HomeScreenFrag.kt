package com.paulmerchants.gold.ui.bottom

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.enums.BbpsType
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.PrePaidCardAdapter
import com.paulmerchants.gold.adapter.UpcomingLoanAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants.BBPS_TYPE
import com.paulmerchants.gold.common.Constants.DUE_LOAN_DATA

import com.paulmerchants.gold.databinding.DummyHomeScreenFragmentBinding
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.model.DueLoans
import com.paulmerchants.gold.model.PrepaidCardModel
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@AndroidEntryPoint
class HomeScreenFrag :
    BaseFragment<DummyHomeScreenFragmentBinding>(DummyHomeScreenFragmentBinding::inflate) {
    private val upcomingLoanAdapter = UpcomingLoanAdapter(::onPayDueClicked)
    private val prePaidCardAdapter = PrePaidCardAdapter()


    lateinit var navController: NavController

    //    private val homeSweetBillsAdapter = HomeSweetBillsAdapter()
    private val TAG = "HomeScreenFrag"

    //    private var isStartAnim = true
    private var isStartAnim = MutableLiveData<Boolean>()
    override fun DummyHomeScreenFragmentBinding.initialize() {
        navController = findNavController()
        setUpComingDueLoans()
    }

    override fun onStart() {
        super.onStart()
        setProfileUi()
        startAnimationOnIcon()
        setUiOnHomeSweetHomeBills()
        handleRechargeAndBillUi()
        setPrepaidCardUi()
    }

    private fun onPayDueClicked(dueLoans: DueLoans) {
        val bundle = Bundle().apply {
            putParcelable(DUE_LOAN_DATA, dueLoans)
        }
        findNavController().navigate(
            R.id.quickPayDialog,
            bundle
        )
    }

    private fun setPrepaidCardUi() {
        val prepaidCard1 = PrepaidCardModel(1, 4, "Prithvi Kumar")
        val prepaidCard2 = PrepaidCardModel(2, 4, "Swati")
        val prepaidCard3 = PrepaidCardModel(3, 4, "Arjun S Narayanan")
        prePaidCardAdapter.submitList(listOf(prepaidCard1, prepaidCard2, prepaidCard3))
        binding.prepaidCardRv.adapter = prePaidCardAdapter
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
        profileHandle()
    }

    private fun profileHandle() {
        binding.searchProfileParent.profileIv.setOnClickListener {
            findNavController().navigate(
                R.id.profileFrag,
                null,
                (activity as MainActivity).navOption
            )
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
        binding.allPaymnetActionParent.moreParent.setOnClickListener {
            binding.allPaymnetActionParent.billsNRechargerParent.hide()
            binding.allPaymnetActionParent.forMoreParentAllAction.show()
            binding.allPaymnetActionParent.backToNormalAction.show()
        }
        binding.allPaymnetActionParent.backToNormalAction.setOnClickListener {
            binding.allPaymnetActionParent.apply {
                billsNRechargerParent.show()
                forMoreParentAllAction.hide()
                backToNormalAction.hide()

            }


        }

        binding.allPaymnetActionParent.homeLoanParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.HomeLoan.type), findNavController())
        }

        binding.allPaymnetActionParent.personalLoanParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.PersonalLoan.type), findNavController())

        }
        binding.allPaymnetActionParent.creditCardParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.CreditCard.type), findNavController())

        }
        binding.allPaymnetActionParent.dthServiceActionParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.DthService.type), findNavController())

        }
        binding.allPaymnetActionParent.dthServiceActionParent2.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.DthService.type), findNavController())

        }
        binding.allPaymnetActionParent.electricityParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Electricity.type), findNavController())

        }
        binding.allPaymnetActionParent.boradBandParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Broadband.type), findNavController())

        }
        binding.allPaymnetActionParent.MobileParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Mobile.type), findNavController())

        }
        binding.allPaymnetActionParent.mobileParent2.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.MobileRecharge.type), findNavController())

        }
        binding.allPaymnetActionParent.mobPostPaidParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.MobilePostpaid.type), findNavController())

        }
        binding.allPaymnetActionParent.ottParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.OttWorld.type), findNavController())

        }
        binding.allPaymnetActionParent.insuranceParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Insurance.type), findNavController())

        }
        binding.allPaymnetActionParent.muncipalTaxParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.MunicipalTax.type), findNavController())

        }
        binding.allPaymnetActionParent.fastTagParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.FastTag.type), findNavController())

        }
        binding.allPaymnetActionParent.challanTraffitParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Challan.type), findNavController())

        }
        binding.allPaymnetActionParent.metroCardParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.MetroCard.type), findNavController())

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
        binding.allPaymnetActionParent.homeSweetHomBillsRv.setUiOnHomeSweetHomeBills(
            requireContext(),
            ::onBillClicked
        )
    }


    private fun onBillClicked(actionItem: ActionItem) {
        AppUtility.onBillClicked(actionItem, findNavController())
    }


}
