package com.paulmerchants.gold.ui.others

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.TransacReceiptBinding
import com.paulmerchants.gold.model.newmodel.PayReceipt
import com.paulmerchants.gold.model.newmodel.RespPayReceipt
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants.ORDER_ID
import com.paulmerchants.gold.utility.Constants.PAYMENT_ID

import com.paulmerchants.gold.utility.showCustomDialogForError
import com.paulmerchants.gold.viewmodels.TxnReceiptViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaidReceiptFrag :
    BaseFragment<TransacReceiptBinding>(TransacReceiptBinding::inflate) {
    var orderId: String? = ""
    var payment_id: String? = ""
    private val txnReceiptViewModel: TxnReceiptViewModel by viewModels()

    override fun TransacReceiptBinding.initialize() {
        orderId = arguments?.getString(ORDER_ID)
        payment_id = arguments?.getString(PAYMENT_ID)
        Log.d("TAG", "initialize: ..........$orderId")
    }

    override fun onStart() {
        super.onStart()
        if (orderId?.isNotEmpty() == true){

            orderId?.let { txnReceiptViewModel.getPaidReceipt(orderId = it, context = requireContext()) }
        }else{
            payment_id?.let {
                txnReceiptViewModel.getPaidReceipt(paymentId = it, context = requireContext())
            }
        }

        txnReceiptViewModel.paidReceipt.observe(viewLifecycleOwner) {
            it?.let {
                if (it.data?.payment_details_dto == null && it.data?.acc_no == null) {


                    requireActivity().showCustomDialogForError(
                        header = "Error!",
                        message = "Unable to retrieve the transaction details. Please try again in few moments.",
                        isClick = {
//                            findNavController().navigateUp()
                            findNavController().popBackStack()
                        })
                }
                setData(it.data)


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

    private fun setData(it: PayReceipt?) {
        binding.apply {
            amountPaid.text = "${getString(R.string.Rs)}${it?.payment_details_dto?.amount ?: "NA"}"
            paymentConfirmIv.setImageResource(
                if (it?.payment_details_dto?.captured == true) {
                    R.drawable.pay_confirm_tick_icon
                } else {
                    R.drawable.baseline_error
                }
            )
            statusPaymnet.text =
                if (it?.payment_details_dto?.captured == true) "SUCCESS!" else "FAIL!"
            dateOfTrans.text = it?.payment_details_dto?.created_at?.let { it1 ->
                AppUtility.getDate(
                    it1
                )
            } ?: "NA"
            transIdNumTv.text = it?.payment_details_dto?.id ?: it?.payment_details_dto?.order_id?: "NA"
            paidFromNameTv.text = it?.payment_details_dto?.method ?: "NA"
            transTypeTv.text = it?.payment_details_dto?.email ?: "NA"
            viewRefNumTv.text = it?.payment_details_dto?.contact ?: "NA"
            paidToNameTv.text = it?.acc_no ?: "NA"
            reasonOFCancelTv.text = it?.payment_details_dto?.error_reason ?: "NA"
            custToNameTv.text = it?.cust_id ?: "NA"
//            cardNumTv.text = ""
//            paidToNameTv.text = ""
//            paidToCardNumTv.text = ""
        }
    }

    override fun onResume() {
        super.onResume()

    }
}