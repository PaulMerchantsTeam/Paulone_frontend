package com.paulmerchants.gold.ui.others

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.LayoutLoanEmiProceedToPayBinding
import com.paulmerchants.gold.databinding.LoanPayInrSecureBinding
import com.paulmerchants.gold.databinding.TransacReceiptBinding
import com.paulmerchants.gold.model.newmodel.RespPaidSingleReceipt
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants.PAYMENT_ID
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.TxnReceiptViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaidReceiptFrag :
    BaseFragment<TransacReceiptBinding>(TransacReceiptBinding::inflate) {
    var paymentId: String? = ""
    private val txnReceiptViewModel: TxnReceiptViewModel by viewModels()

    override fun TransacReceiptBinding.initialize() {
        paymentId = arguments?.getString(PAYMENT_ID)
    }


    override fun onStart() {
        super.onStart()
        paymentId?.let {
            txnReceiptViewModel.getPaidReceipt(
                (activity as MainActivity).appSharedPref,
                it
            )
        }
        txnReceiptViewModel.paidReceipt.observe(viewLifecycleOwner) {
            it?.let {
                setData(it)
            }
        }

        binding.phoneQueryTv.setOnClickListener {

        }
        binding.shareTv.setOnClickListener {

        }
        binding.downlaodBtn.setOnClickListener {

        }
        binding.headerMain.titlePageTv.text =""
        binding.headerMain.backIv.setOnClickListener {
            findNavController().popBackStack(R.id.paidReceiptFrag, true)
            findNavController().navigate(R.id.homeScreenFrag)
        }
    }

    private fun setData(it: RespPaidSingleReceipt) {
        binding.apply {
            amountPaid.text = it.data.amount.toString()
            paymentConfirmIv.setImageResource(R.drawable.pay_confirm_tick_icon)
            statusPaymnet.text = if (it.data.captured) "SUCCESS!" else "FAIL!"
            dateOfTrans.text = AppUtility.formatDateFromMilliSec(it.data.created_at).toString()
            transIdNumTv.text = it.data.id
//            transTypeTv.text = ""
//            viewRefNumTv.text = ""
//            paidFromNameTv.text = it.data.method
//            cardNumTv.text = ""
//            paidToNameTv.text = ""
//            paidToCardNumTv.text = ""
        }
    }

    override fun onResume() {
        super.onResume()

    }
}