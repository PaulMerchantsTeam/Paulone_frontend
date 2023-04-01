package com.paulmerchants.gold.ui.bottom

import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.MenuServicesAdapter
import com.paulmerchants.gold.adapter.TypeServiceAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.DummyMenuScreenFragmentBinding
import com.paulmerchants.gold.databinding.MainScreenFragmentBinding
import com.paulmerchants.gold.databinding.MenuScreenFragmentBinding
import com.paulmerchants.gold.model.MenuServices
import com.paulmerchants.gold.model.TypeService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuScreenFrag : BaseFragment<DummyMenuScreenFragmentBinding>(DummyMenuScreenFragmentBinding::inflate) {

    private val menuServiceAdapter = MenuServicesAdapter()
    private val typeServicesAdapter = TypeServiceAdapter()

    override fun DummyMenuScreenFragmentBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        setTypeServiceUi()
        setServicesUi()

    }

    private fun setTypeServiceUi() {
        val ts1 = TypeService(1, getString(R.string.services))
        val ts2 = TypeService(2, getString(R.string.transaction))
        val ts3 = TypeService(3, getString(R.string.settings))
        val ts4 = TypeService(4, getString(R.string.rewards))
        val list = listOf(ts1, ts2, ts3, ts4)
        typeServicesAdapter.submitList(list)
        binding.optionItemMenuRv.adapter = typeServicesAdapter
    }

    private fun setServicesUi() {
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