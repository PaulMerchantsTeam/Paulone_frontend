package com.paulmerchants.gold.ui.others

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.PaymentModeAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.common.PayMethod
import com.paulmerchants.gold.databinding.PaymentModesBinding
import com.paulmerchants.gold.model.PayModes
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentModesFrag : BaseFragment<PaymentModesBinding>(PaymentModesBinding::inflate) {

    private val payModeAdapterPCards = PaymentModeAdapter(::pyNowButtonClicked)
    private val payModeAdapterUpi = PaymentModeAdapter(::pyNowButtonClicked)
    private val payModeAdapterWallet = PaymentModeAdapter(::pyNowButtonClicked)
    private val payModeAdapterDebCreditCard = PaymentModeAdapter(::pyNowButtonClicked)
    private var amountToPay: Double? = 0.0
    private fun pyNowButtonClicked(payModes: PayModes) {

    }

    override fun PaymentModesBinding.initialize() {
        amountToPay = arguments?.getDouble(Constants.AMOUNT_PAYABLE)
        Log.e("TAGGGGGGG", "initialize: -------$amountToPay")
        modifyHeaders()

    }

    override fun onStart() {
        super.onStart()
        setPrepaidCards()
        setUpi()
        setWallet()
        setDebitCreditCard()
    }

    private fun setPrepaidCards() {
        val list = listOf(
            PayModes(
                PayMethod.PREPAID_CARD.id,
                getString(R.string.prepaid_card_small),
                R.drawable.paul_pay_small,
                "xxxx xxxx xxxx 4536"
            )
        )
        payModeAdapterPCards.submitList(list)
        binding.prepaidCardRv.adapter = payModeAdapterPCards
    }

    private fun setUpi() {
        val list = listOf(
            PayModes(
                PayMethod.BHIM.id, getString(R.string.pay_through_upi), R.drawable.bhm_img, ""
            )
        )
        payModeAdapterUpi.submitList(list)
        binding.upiRv.adapter = payModeAdapterUpi
    }

    private fun setWallet() {
        val list = listOf(
            PayModes(
                PayMethod.PAYTM.id, getString(R.string.paytm), R.drawable.bhm_img, "+91 xxxxxxx546"
            ), PayModes(
                PayMethod.GPAY.id, getString(R.string.gpay), R.drawable.google_pay, "+91 xxxxxxx546"
            ), PayModes(
                PayMethod.PHONE_PAY.id,
                getString(R.string.phone_pay),
                R.drawable.phone_pay,
                "+91 xxxxxxx546"
            )
        )
        payModeAdapterWallet.submitList(list)
        binding.walletRv.adapter = payModeAdapterWallet
    }

    private fun setDebitCreditCard() {
        val list = listOf(
            PayModes(
                PayMethod.DEBIT_CARD.id,
                getString(R.string.axis),
                R.drawable.visa_inc,
                "xxxx xxxx xxxx 4253"
            ), PayModes(
                PayMethod.CREDIT_CARD.id,
                getString(R.string.hdfc),
                R.drawable.mastercard,
                "xxxx xxxx xxxx 2318"
            )
        )
        payModeAdapterDebCreditCard.submitList(list)
        binding.debitCredRv.adapter = payModeAdapterDebCreditCard
    }

    private fun modifyHeaders() {
        binding.apply {
            binding.headerBillMore.apply {
                backIv.setOnClickListener {
                    findNavController().navigateUp()
                }
                titlePageTv.text = getString(R.string.pay_modes)
                endIconIv.setImageResource(R.drawable.quest_circle)
                endIconIv.hide()
            }
        }
    }


}