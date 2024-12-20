package com.paulmerchants.gold.ui.others

import androidx.appcompat.widget.ViewUtils
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment

import com.paulmerchants.gold.databinding.ResetCardPinBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.showResetPinSuccessDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResetCardPinFrag : BaseFragment<ResetCardPinBinding>(ResetCardPinBinding::inflate) {

    override fun ResetCardPinBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()
    }

    private fun modifyHeaders() {
        binding.headerResetPin.titlePageTv.text = getString(R.string.reset_card_pin)
        binding.headerResetPin.titlePageTv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.splash_screen_one
            )
        )
        binding.headerResetPin.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.proceedBtn.setOnClickListener {
            showResetPinSuccessDialog()
        }
    }


}