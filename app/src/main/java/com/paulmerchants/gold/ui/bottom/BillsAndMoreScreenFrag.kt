package com.paulmerchants.gold.ui.bottom

import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.BillNMoreScreenFragmentBinding
import com.paulmerchants.gold.databinding.MainScreenFragmentBinding
import com.paulmerchants.gold.enums.BbpsType
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.ui.others.BillsFragment
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.setUiOnHomeSweetHomeBills
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillsAndMoreScreenFrag :
    BaseFragment<BillNMoreScreenFragmentBinding>(BillNMoreScreenFragmentBinding::inflate) {
    lateinit var navController: NavController
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
        binding.headerBillMore.backIv.hide()
        setUiOnHomeSweetHomeBills()
        binding.apply {
            billSRechargeMain.dthServiceActionParent2.setOnClickListener {
                AppUtility.onBillClicked(ActionItem(BbpsType.DthService.type), findNavController())

            }
            billSRechargeMain.mobileParent2.setOnClickListener {
                AppUtility.onBillClicked(ActionItem(BbpsType.MobileRecharge.type), findNavController())

            }
            billSRechargeMain.mobPostPaidParent.setOnClickListener {
                AppUtility.onBillClicked(ActionItem(BbpsType.MobilePostpaid.type), findNavController())

            }
            billSRechargeMain.ottParent.setOnClickListener {
                AppUtility.onBillClicked(ActionItem(BbpsType.OttWorld.type), findNavController())

            }
            billSRechargeMain.insuranceParent.setOnClickListener {
                AppUtility.onBillClicked(ActionItem(BbpsType.Insurance.type), findNavController())

            }
            billSRechargeMain.muncipalTaxParent.setOnClickListener {
                Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()

            }
            billSRechargeMain.fastTagParent.setOnClickListener {
                AppUtility.onBillClicked(ActionItem(BbpsType.FastTag.type), findNavController())

            }
            billSRechargeMain.challanTraffitParent.setOnClickListener {
                Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()

            }
            billSRechargeMain.metroCardIv.setOnClickListener {
                AppUtility.onBillClicked(ActionItem(BbpsType.MetroCard.type), findNavController())

            }

        }

    }

    private fun setUiOnHomeSweetHomeBills() {
        binding.billSRechargeMain.homeSweetHomBillsRv.setUiOnHomeSweetHomeBills(requireContext(), ::onBillClicked)
    }
    private fun onBillClicked(actionItem: ActionItem){
        AppUtility.onBillClicked(actionItem,findNavController())
    }

}