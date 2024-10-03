package com.paulmerchants.gold.ui.others

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txnReceiptViewModel.paidReceipt.observe(viewLifecycleOwner) {
            it?.let {
                setData(it)
            }
        }
    }


    override fun onStart() {
        super.onStart()

        paymentId?.let {
            txnReceiptViewModel.getPaidReceipt(
                it
            )
        }

        binding.apply {

            gotoHomeBtn.setOnClickListener {
                findNavController().popBackStack()
                findNavController().navigate(R.id.homeScreenFrag)
      }
            needHelpBtn.setOnClickListener{
                val phone = "18001371333"
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                startActivity(intent)
            }
            downLoadReceiptTv.setOnClickListener{

                val screenBitmap = AppUtility.getScreenBitmap( paymentConfirmMainParent)
                val pdfWidth = 500f
                val pdfHeight = 870f
                AppUtility.saveAsPdf(
                    requireContext(),
                    pdfWidth,
                    pdfHeight,
                    screenBitmap,
                    R.color.open_loans
                )
            }
            lifecycleScope.launch {
                delay(2000)
                loadingParent.hide()

                    paymentConfirmedParent.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(), R.anim.slide_up
                        ))


                paymentConfirmedParent.show()

                paymentConfirmedParent.clearAnimation()
            }

        }
    }




   // 82233213123
    private fun setData(it: RespPayReceipt) {
        binding.apply {
            paymentConfirmIv.setImageResource(
                if (it.data.entityPayment?.captured == true) {
                    R.drawable.pay_confirm_tick_icon
                } else {
                    R.drawable.baseline_error
                }
            )
            paymetConfirmTv.text =
                if (it.data.entityPayment?.captured == true) {
                   "PAYMENT CONFIRMED!!"
                } else {
                  "PAYMENT FAILED!!"
                }

            transIdTv.text = it.data.entityPayment?.paymentId?: "NA"
            accountNoTv.text =it.data.accNo?: "NA"
            customerNameTv.text =  AppSharedPref.getStringValue(
                com.paulmerchants.gold.utility.Constants.CUSTOMER_NAME,
            )?.substringBefore(" ")?: "NA"
//            transDteAndTimeTv.text = it.data.entityPayment?.created_at
            transDteAndTimeTv.text = it.data.entityPayment?.updated_at?.let { it1 ->
                AppUtility.getDate (
                    it1
                )
            }?: "NA"
            modeOfPaymentTv.text = it.data.entityPayment?.method?: "NA"
            amountPaidTv.text =  "${getString(R.string.Rs)} ${it.data.entityPayment?.amount?.let { it1 ->
                AppUtility.getTwoDigitAfterDecimal(
                    it1.toDouble())
            }?: "NA"}"


        }
    }
}