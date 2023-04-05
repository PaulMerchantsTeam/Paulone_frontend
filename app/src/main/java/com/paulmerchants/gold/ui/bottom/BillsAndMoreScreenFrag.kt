package com.paulmerchants.gold.ui.bottom

import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.BillNMoreScreenFragmentBinding
import com.paulmerchants.gold.databinding.MainScreenFragmentBinding
import com.paulmerchants.gold.ui.others.BillsFragment
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.setUiOnHomeSweetHomeBills
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillsAndMoreScreenFrag :
    BaseFragment<BillNMoreScreenFragmentBinding>(BillNMoreScreenFragmentBinding::inflate) {
lateinit var navController:NavController
    override fun BillNMoreScreenFragmentBinding.initialize() {
        navController = findNavController()
        binding.headerBillMore.apply {
            titlePageTv.text = getString(R.string.bills_amp_more)
            subTitle.show()
            subTitle.text = getString(R.string.one_place_for_everything)
        }
        binding.billSRechargeMain.apply {
            otherEMisParent.hide()
            billsNRechargerParent.hide()
            billsAndRechargeTv2.hide()
            billsAndRechargeTv.hide()
            forMoreParentAllAction.show()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.headerBillMore.backIv.setOnClickListener {

        }

        setUiOnHomeSweetHomeBills()
    }

    private fun setUiOnHomeSweetHomeBills() {
        binding.billSRechargeMain.homeSweetHomBillsRv.setUiOnHomeSweetHomeBills(requireContext())
    }

}