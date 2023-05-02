package com.paulmerchants.gold.ui.bottom

import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.MenuServicesAdapter
import com.paulmerchants.gold.adapter.TransacDoneAdapter
import com.paulmerchants.gold.adapter.TypeServiceAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.customviews.CustomViews
import com.paulmerchants.gold.databinding.DummyMenuScreenFragmentBinding
import com.paulmerchants.gold.model.MenuServices
import com.paulmerchants.gold.model.TransDoneModel
import com.paulmerchants.gold.model.TypeService
import com.paulmerchants.gold.enums.ServiceType
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuScreenFrag :
    BaseFragment<DummyMenuScreenFragmentBinding>(DummyMenuScreenFragmentBinding::inflate) {

    private var myView: CustomViews? = null

    private val menuServiceAdapter = MenuServicesAdapter(::onMenuServiceClicked,::onMenuServiceClicedTwo,::onMenuServiceTitleClicked)


    private fun onMenuServiceClicedTwo(menuServices: MenuServices) {
        Log.d("TAG", "onMenuServiceClicked: /////${menuServices.serviceId}")
        when (menuServices.serviceId) {
            1 -> {
                findNavController().navigate(R.id.applyLoanForNewUser)
            }
            2 -> {
                val bundle = Bundle().apply {
                    putString("Other","other")
                }
                findNavController().navigate(R.id.billsAndMoreScreenFrag,bundle)
            }
            3 -> {
                findNavController().navigate(R.id.billsFragment)
            }
            4 -> {
                findNavController().navigate(R.id.pcFrag)
            }
            5 -> {
            }
            6 -> {
            }
            7 -> {
            }
            8 -> {
            }
            9 -> {
            }
            10 -> {
            }
            11 -> {
            }
            12 -> {
            }
            13 -> {
            }
            else -> {
                Log.d("TAG", "onMenuServiceClicked: [=false")
            }
        }
    }

    private fun onMenuServiceClicked(menuServices: MenuServices) {
        Log.d("TAG", "onMenuServiceClicked: /////${menuServices.serviceId}")
        when (menuServices.serviceId) {
            1 -> {
                findNavController().navigate(R.id.goldLoanScreenFrag)
            }
            2 -> {
                findNavController().navigate(R.id.billsAndMoreScreenFrag)
            }
            3 -> {
                findNavController().navigate(R.id.goldLoanScreenFrag)
            }
            4 -> {
                findNavController().navigate(R.id.pcFrag)
            }
            5 -> {
                findNavController().navigate(R.id.creditScoreScreenFrag)
            }
            6 -> {
            }
            7 -> {
            }
            8 -> {
            }
            9 -> {
            }
            10 -> {
            }
            11 -> {
            }
            12 -> {
            }
            13 -> {
            }
            else -> {
                Log.d("TAG", "onMenuServiceClicked: [=false")
            }
        }

    }

    private fun onMenuServiceTitleClicked(menuServices: MenuServices){

    }

    private val transacDoneAdapter = TransacDoneAdapter()
    private val typeServicesAdapter = TypeServiceAdapter(::onTypeServiceClicked)
    override fun DummyMenuScreenFragmentBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        setTypeServiceUi()
        setServicesUi()
        myView = CustomViews(requireContext(), binding.linearOne)
        binding.linearOne.addView(myView)
        navigateToAnotherScreen()
    }

    private fun navigateToAnotherScreen() {
//        binding.prepaidMenuCard.setOnClickListener {
//            findNavController().navigate(R.id.pcFrag)
//        }
        binding.viewProfileVBtn.setOnClickListener {
            findNavController().navigate(R.id.profileFrag)
        }
        binding.prepaidMenuCard.setOnClickListener {
            findNavController().navigate(R.id.complaintRegister)
        }
        binding.neddSuppMenuCard.setOnClickListener {
            findNavController().navigate(R.id.transactionDetailFrag)
        }
    }

    private fun setTypeServiceUi() {
        val ts1 = TypeService(0, getString(R.string.services))
        val ts2 = TypeService(1, getString(R.string.transaction))
        val ts3 = TypeService(2, getString(R.string.settings))
        val ts4 = TypeService(3, getString(R.string.rewards))
        val list = listOf(ts1, ts2, ts3, ts4)
        typeServicesAdapter.submitList(list)
        binding.optionItemMenuRv.adapter = typeServicesAdapter
        typeServicesAdapter.setSelectedPosition(0)
    }

    private fun onTypeServiceClicked(typeService: TypeService) {
        typeServicesAdapter.setSelectedPosition(typeService.id)
        typeServicesAdapter.notifyDataSetChanged()
        when (typeService.id) {
            ServiceType.SERVICES.type -> {
                setServicesUi()
            }
            ServiceType.TRANSACTION.type -> {
                setTransactionUi()
            }
            ServiceType.SETTINGS.type -> {
//                typeServicesAdapter.submitList(null)
                setSettingsUi()
            }
            ServiceType.REWARDS.type -> {
                setRewardsUi()
            }
        }
    }

    private fun setRewardsUi() {
        //no data available
        binding.transacSearchView.hide()
        binding.servicesRv.hide()
        binding.lotteAwards.root.show()
    }

    private fun setSettingsUi() {
        binding.lotteAwards.root.hide()
        binding.transacSearchView.hide()
        binding.servicesRv.show()
        val service1 = MenuServices(
            6,
            getString(R.string.history),
            getString(R.string.view_trans),
            ""
        )
        val service2 = MenuServices(
            7,
            getString(R.string.bank_acc),
            getString(R.string.upi_setting),
            getString(R.string.card_setting)
        )
        val service3 = MenuServices(
            8,
            getString(R.string.acc_sett),
            getString(R.string.man_yr_Account),
            getString(R.string.add_or_mange_add)
        )
        val service4 = MenuServices(
            9,
            getString(R.string.all_abt_us),
            getString(R.string.wht_we_r),
            ""
        )
        val service5 =
            MenuServices(
                10,
                "",
                getString(R.string.our_priv_policy),
                ""
            )

        val service6 =
            MenuServices(
                11,
                "",
                getString(R.string.we_beleive_in_sec),
                ""
            )
        val service7 =
            MenuServices(
                12,
                "",
                getString(R.string.termNCondn),
                ""
            )
        val service8 =
            MenuServices(
                13,
                "",
                getString(R.string.faqs),
                ""
            )
        val listService =
            listOf(service1, service2, service3, service4, service5, service6, service7, service8)
        menuServiceAdapter.submitList(listService)
        binding.servicesRv.adapter = menuServiceAdapter
    }

    private fun setTransactionUi() {
        binding.transacSearchView.show()
        binding.servicesRv.show()
        binding.lotteAwards.root.hide()
        val trans1 = TransDoneModel(
            0,
            R.drawable.transc_gold_icon,
            1,
            "Gold Loan",
            "3500",
            "Feb 1st, 2023 at 7:30 pm"
        )

        val trans2 = TransDoneModel(
            0,
            R.drawable.transc_gold_icon,
            2,
            "Gold Loan",
            "3500",
            "Feb 1st, 2023 at 7:30 pm"
        )

        val trans3 = TransDoneModel(
            3,
            R.drawable.transc_gold_icon,
            3,
            "Gold Loan",
            "3500",
            "Feb 1st, 2023 at 7:30 pm"
        )
        val listService =
            listOf(trans1, trans2, trans3)
        transacDoneAdapter.submitList(listService)
        binding.servicesRv.adapter = transacDoneAdapter
    }


    private fun setServicesUi() {
        binding.transacSearchView.hide()
        binding.servicesRv.show()
        binding.lotteAwards.root.hide()
        val service1 = MenuServices(
            1,
            getString(R.string.gold_loan),
            getString(R.string.loan_overview),
            getString(R.string.loan_renewed)
        )
        val service2 = MenuServices(
            2,
            getString(R.string.bills_amp_more),
            getString(R.string.recharge),
            getString(R.string.other_bills)
        )
        val service3 = MenuServices(
            3,
            getString(R.string.other_emis),
            getString(R.string.gold_loan),
            getString(R.string.other_emi_pay)
        )
        val service4 = MenuServices(
            4,
            getString(R.string.prepaid_card),
            getString(R.string.transaction),
            getString(R.string.recharge)
        )
        val service5 =
            MenuServices(5, getString(R.string.others), getString(R.string.credit_score), "")

        val listService = listOf(service1, service2, service3, service4, service5)
        menuServiceAdapter.submitList(listService)
        binding.servicesRv.adapter = menuServiceAdapter
    }


}