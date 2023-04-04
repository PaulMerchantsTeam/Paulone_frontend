package com.paulmerchants.gold.ui.others

import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.LocateUsScreenFragmentBinding
import com.paulmerchants.gold.databinding.PrepaidScreenFragBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PcFrag : BaseFragment<PrepaidScreenFragBinding>(PrepaidScreenFragBinding::inflate) {

    override fun PrepaidScreenFragBinding.initialize() {
        showCard()
    }

    private fun showCard() {
        var isShowCard = false
        binding.viewCardBtn.setOnClickListener {
            if (!isShowCard) {
                binding.apply {
                    prepaidLayoutParent.apply {
                        parentCardInfo.setBackgroundResource(R.drawable.rec_frch_blue_solid)
                        expTv.show()
                        cvvTv.show()
                        getDetailsBtn.show()
                        otpDescTv.show()
                        viewCardBtn.text = ""
                        viewCardBtn.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.arrow_up_black,
                            0
                        )
                    }
                }
                isShowCard = true
            } else {
                binding.apply {
                    prepaidLayoutParent.apply {
                        parentCardInfo.setBackgroundResource(R.drawable.prepaid_card_back_half)
                        expTv.hide()
                        cvvTv.hide()
                        getDetailsBtn.hide()
                        otpDescTv.hide()
                        viewCardBtn.text = getString(R.string.view_card)
                        viewCardBtn.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            0
                        )
                    }
                }
                isShowCard = false
            }
        }
    }

}