package com.paulmerchants.gold.ui.others

import android.content.Intent
import android.net.Uri
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.LoanEmiPaymentConfirmedBinding
import com.paulmerchants.gold.model.newmodel.RespPayReceipt
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.TxnReceiptViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PaymentConfirmed :
    BaseFragment<LoanEmiPaymentConfirmedBinding>(LoanEmiPaymentConfirmedBinding::inflate) {
    var headerValue :String? = null
    var paymentId :String? = null
    private val txnReceiptViewModel: TxnReceiptViewModel by viewModels()
    override fun LoanEmiPaymentConfirmedBinding.initialize() {

        headerValue = arguments?.getString(Constants.BBPS_HEADER, "")

        paymentId = arguments?.getString(com.paulmerchants.gold.utility.Constants.PAYMENT_ID)

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
    }
    override fun onResume() {
        super.onResume()
        binding.apply {
            headerLoanConfirmed.backIv.hide()
            headerLoanConfirmed.endIconIv.hide()
//            headerLoanConfirmed.titlePageTv.setText(headerValue.toString())
//            headerLoanConfirmed.endIconIv.setImageResource(R.drawable.bbps)
            gotoHomeBtn.setOnClickListener {
                findNavController().popBackStack()
                findNavController().navigate(R.id.homeScreenFrag)
      }
            needHelpBtn.setOnClickListener{
                val phone = "18001371333"
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                startActivity(intent)
            }
            lifecycleScope.launch {
                delay(2000)
                loadingParent.hide()
                paymentConfirmedParent.show()
                paymentConfirmedParent.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(), R.anim.slide_up
                        ))
            }

        }
    }
   // 82233213123
    private fun setData(it: RespPayReceipt) {
        binding.apply {

            transIdTv.text = it.data.entityPayment?.paymentId
            accountNoTv.text =it.data.accNo
            customerNameTv.text =  AppSharedPref.getStringValue(
                com.paulmerchants.gold.utility.Constants.CUSTOMER_NAME,
            )?.substringBefore(" ")
//            transDteAndTimeTv.text = it.data.entityPayment?.created_at
            transDteAndTimeTv.text = it.data.entityPayment?.updated_at?.let { it1 ->
                AppUtility.getDate (
                    it1
                )
            }
            modeOfPaymentTv.text = it.data.entityPayment?.method
            amountPaidTv.text =  "${getString(R.string.Rs)} ${it.data.entityPayment?.amount?.let { it1 ->
                AppUtility.getTwoDigitAfterDecimal(
                    it1.toDouble())
            }}"


        }
    }
}