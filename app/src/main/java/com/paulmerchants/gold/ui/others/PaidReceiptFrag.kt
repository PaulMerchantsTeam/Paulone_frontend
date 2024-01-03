package com.paulmerchants.gold.ui.others

import android.os.Bundle
import android.util.Log
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
        Log.d("TAG", "initialize: ..........$paymentId")
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
            AppUtility.dialer(requireContext(), "18001371333")
        }

        binding.shareTv.setOnClickListener {
            try {
                AppUtility.takeScreenShot(binding.parentTxn, requireContext())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.downlaodBtn.setOnClickListener {
            val screenBitmap =
                AppUtility.getScreenBitmap(binding.parentTxn, R.color.white)
            val pdfWidth = 500f
            val pdfHeight = 800f
            AppUtility.saveAsPdf(
                requireContext(),
                pdfWidth,
                pdfHeight,
                screenBitmap,
                R.color.white
            )
        }
        binding.headerMain.titlePageTv.text = ""
        binding.headerMain.backIv.setOnClickListener {
            findNavController().popBackStack(R.id.paidReceiptFrag, true)
            findNavController().navigate(R.id.homeScreenFrag)
        }
    }

    private fun setData(it: RespPaidSingleReceipt) {
        binding.apply {
            amountPaid.text = "${getString(R.string.Rs)}${it.data.paymentDetailsDTO.amount}"
            if (it.data.paymentDetailsDTO.captured) {
                paymentConfirmIv.setImageResource(R.drawable.pay_confirm_tick_icon)
            } else {
                paymentConfirmIv.setImageResource(R.drawable.baseline_error)
            }
            statusPaymnet.text = if (it.data.paymentDetailsDTO.captured) "SUCCESS!" else "FAIL!"
            dateOfTrans.text =
                AppUtility.formatDateFromMilliSec(it.data.paymentDetailsDTO.created_at).toString()
            transIdNumTv.text = it.data.paymentDetailsDTO.id
            paidFromNameTv.text = it.data.paymentDetailsDTO.method
            transTypeTv.text = it.data.paymentDetailsDTO.email
            viewRefNumTv.text = it.data.paymentDetailsDTO.contact
            paidToNameTv.text = it.data.accNo
//            cardNumTv.text = ""
//            paidToNameTv.text = ""
//            paidToCardNumTv.text = ""
        }
    }

    override fun onResume() {
        super.onResume()

    }
}