package com.paulmerchants.gold.ui.others

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.MenuServicesAdapter
import com.paulmerchants.gold.adapter.PrepaidMainAdapter
import com.paulmerchants.gold.adapter.TransDoneAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.PrepaidScreenFragBinding
import com.paulmerchants.gold.enums.ServiceType
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.model.MenuServices
import com.paulmerchants.gold.model.TransDoneModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrepaidCardMainFrag :
    BaseFragment<PrepaidScreenFragBinding>(PrepaidScreenFragBinding::inflate) {

    private val menuServiceAdapter = MenuServicesAdapter(
        ::onMenuServiceClicked,
        ::onMenuServiceClicedTwo,
        ::onMenuServiceTitleClicked
    )
    private val prepaidMainAdapter = PrepaidMainAdapter(::viewBtnClicked)
    private val transacDoneAdapter = TransDoneAdapter(::onTransactionClicked)
    private fun onTransactionClicked(transDoneModel: TransDoneModel) {
        findNavController().navigate(R.id.transactionDoneScreenFrag)
    }

    private fun viewBtnClicked(actionItem: ActionItem, isShow: Boolean) {

    }

    private fun onMenuServiceClicked(menuServices: MenuServices) {
        when (menuServices.serviceId) {
            ServiceType.ACTION_CHANG_CARD_PIN.type -> {
                findNavController().navigate(R.id.action_to_resetPinCard)
                Log.d("TAG", "onMenuServiceClicked: ...1....${menuServices.titleName}")
            }

            ServiceType.ACTION_CARD_LOST_OR_STOLEN.type -> {
                Log.d("TAG", "onMenuServiceClicked: ...2....${menuServices.titleName}")
            }
        }
    }

    private fun onMenuServiceClicedTwo(menuServices: MenuServices) {
        when (menuServices.serviceId) {
            ServiceType.ACTION_CHANG_CARD_PIN.type -> {

                Log.d("TAG", "onMenuServiceClicked: ...3....${menuServices.titleName}")
            }

            ServiceType.ACTION_CARD_LOST_OR_STOLEN.type -> {
                Log.d("TAG", "onMenuServiceClicked: ...4....${menuServices.titleName}")
            }
        }
    }


    private fun onMenuServiceTitleClicked(menuServices: MenuServices) {
        when (menuServices.serviceId) {

        }
    }

    override fun PrepaidScreenFragBinding.initialize() {
    }

    override fun onStart() {
        super.onStart()
        initUi()
        showCard()
    }

    private fun initUi() {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showCard() {
        val act1 = ActionItem(1, 0, "Prithvi Kumar")
        val act2 = ActionItem(1, 0, "Arjun s Narayanan")

        prepaidMainAdapter.submitList(listOf(act1, act2))
        binding.rvPrepaidCard.adapter = prepaidMainAdapter
        setTransactionUi()
        setServicesUi()
    }

    private fun setTransactionUi() {
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
        binding.recentTransactionRv.adapter = transacDoneAdapter
    }

    private fun setServicesUi() {
        val service1 = MenuServices(
            ServiceType.ACTION_CHANG_CARD_PIN.type,
            getString(R.string.services),
            getString(R.string.change_card_pin),
            getString(R.string.reissue_new_card)
        )
        val service2 = MenuServices(
            ServiceType.ACTION_CARD_LOST_OR_STOLEN.type,
            getString(R.string.theft_or_closure),
            getString(R.string.card_lost_stollen),
            getString(R.string.terminated_card_srvc)
        )
        val listService = listOf(service1, service2)
        menuServiceAdapter.submitList(listService)
        binding.servicesRv.adapter = menuServiceAdapter
    }
}