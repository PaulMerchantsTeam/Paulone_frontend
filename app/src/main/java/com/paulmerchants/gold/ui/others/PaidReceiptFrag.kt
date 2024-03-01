package com.paulmerchants.gold.ui.others

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.TransacReceiptBinding
import com.paulmerchants.gold.model.newmodel.RespPayReceipt
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants.PAYMENT_ID
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
//            val screenBitmap =
//                AppUtility.getScreenBitmap(binding.parentTxn, R.color.white)
//            val pdfWidth = 500f
//            val pdfHeight = 800f
//            AppUtility.saveAsPdf(
//                requireContext(),
//                pdfWidth,
//                pdfHeight,
//                screenBitmap,
//                R.color.white
//            )
        }
        binding.headerMain.titlePageTv.text = ""
        binding.headerMain.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setData(it: RespPayReceipt) {
        binding.apply {
            amountPaid.text = "${getString(R.string.Rs)}${it.data.entityPayment?.amount}"
            paymentConfirmIv.setImageResource(
                if (it.data.entityPayment?.captured == true) {
                    R.drawable.pay_confirm_tick_icon
                } else {
                    R.drawable.baseline_error
                }
            )
            statusPaymnet.text =
                if (it.data.entityPayment?.captured == true) "SUCCESS!" else "FAIL!"
            dateOfTrans.text = it.data.entityPayment?.created_at
            transIdNumTv.text = it.data.entityPayment?.paymentId ?: "NA"
            paidFromNameTv.text = it.data.entityPayment?.method
            transTypeTv.text = it.data.entityPayment?.email
            viewRefNumTv.text = it.data.entityPayment?.contact
            paidToNameTv.text = it.data.accNo ?: "NA"
            reasonOFCancelTv.text = it.data.entityPayment?.error_reason
//            cardNumTv.text = ""
//            paidToNameTv.text = ""
//            paidToCardNumTv.text = ""
        }
    }

    override fun onResume() {
        super.onResume()

    }
}