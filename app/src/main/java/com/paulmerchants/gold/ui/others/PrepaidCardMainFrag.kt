package com.paulmerchants.gold.ui.others

import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.MenuServicesAdapter
import com.paulmerchants.gold.adapter.PrepaidMainAdapter
import com.paulmerchants.gold.adapter.TransacDoneAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.PrepaidScreenFragBinding
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.model.TransDoneModel
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrepaidCardMainFrag :
    BaseFragment<PrepaidScreenFragBinding>(PrepaidScreenFragBinding::inflate) {

    private val prepaidMainAdapter = PrepaidMainAdapter(::viewBtnClicked)
    private val transacDoneAdapter = TransacDoneAdapter()
    private fun viewBtnClicked(actionItem: ActionItem, isShow: Boolean) {

    }

    override fun PrepaidScreenFragBinding.initialize() {
        showCard()
    }

    private fun showCard() {
        val act1 = ActionItem(1, 0, "Prithvi Kumar")
        val act2 = ActionItem(1, 0, "Arjun s Narayanan")

        prepaidMainAdapter.submitList(listOf(act1, act2))
        binding.rvPrepaidCard.adapter = prepaidMainAdapter
        setTransactionUi()


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
}