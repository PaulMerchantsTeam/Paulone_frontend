package com.paulmerchants.gold.ui.bottom

import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.BillNMoreScreenFragmentBinding
import com.paulmerchants.gold.enums.BbpsType
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.utility.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillsAndMoreScreenFrag :
    BaseFragment<BillNMoreScreenFragmentBinding>(BillNMoreScreenFragmentBinding::inflate) {
    lateinit var navController: NavController
    lateinit var otherValue: String

    override fun BillNMoreScreenFragmentBinding.initialize() {
        otherValue = arguments?.getString("Other","").toString()
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
        startAnimationOnIcon()
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
       if(otherValue == "other"){
           otherBills()
       }



    }

    private fun setUiOnHomeSweetHomeBills() {
        binding.billSRechargeMain.homeSweetHomBillsRv.setUiOnHomeSweetHomeBills(requireContext(), ::onBillClicked)
    }
    private fun onBillClicked(actionItem: ActionItem){
        AppUtility.onBillClicked(actionItem,findNavController())
    }

    private fun startAnimationOnIcon() {
        binding.billSRechargeMain.apply {
            goldIv.startCustomAnimation(R.drawable.anim_gold_icon)
            dthIV.startCustomAnimation(R.drawable.anim_dth_service_icon)
            elecIv.startCustomAnimation(R.drawable.anim_elec_icon)
            boradBandIv.startCustomAnimation(R.drawable.anim_broadband_icon)
            mobileIv.startCustomAnimation(R.drawable.anim_mobile_icon)
            mob2Iv.startCustomAnimation(R.drawable.anim_mobile_icon)
            mobPostIv.startCustomAnimation(R.drawable.anim_post_paid_icon)
            ottIv.startCustomAnimation(R.drawable.anim_ott_icon)
            dthIV2.startCustomAnimation(R.drawable.anim_dth_service_icon)
            isuranceIv.startCustomAnimation(R.drawable.anim_insurance_icon)
            mucipalIv.startCustomAnimation(R.drawable.anim_munciple_tax)
            fastIv.startCustomAnimation(R.drawable.anim_fastag_icon)
            challanIv.startCustomAnimation(R.drawable.anim_challan_icon)
            metroCardIv.startCustomAnimation(R.drawable.anim_metro_card)

        }
    }

    private fun otherBills(){
        binding.apply {
           billSRechargeMain.otherEMisParent.show()
            billSRechargeMain. billsNRechargerParent.hide()
            billSRechargeMain. billsAndRechargeTv2.hide()
            billSRechargeMain. billsAndRechargeTv.hide()
            billSRechargeMain. forMoreParentAllAction.hide()

        }
    }

}