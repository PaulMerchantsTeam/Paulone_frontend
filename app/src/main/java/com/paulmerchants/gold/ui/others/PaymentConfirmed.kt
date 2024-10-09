package com.paulmerchants.gold.ui.others

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseActivity
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.LoanEmiPaymentConfirmedBinding
import com.paulmerchants.gold.databinding.PaymentsModeNewBinding
import com.paulmerchants.gold.model.newmodel.RespPayReceipt
import com.paulmerchants.gold.model.newmodel.RespPayReceiptNew
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.ui.PaymentActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.PaymentViewModel
import com.paulmerchants.gold.viewmodels.TxnReceiptViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PaymentConfirmed :
    BaseActivity<TxnReceiptViewModel, LoanEmiPaymentConfirmedBinding>()
      {
    var headerValue :String? = null
    var paymentId :String? = null

          override fun getViewBinding() = LoanEmiPaymentConfirmedBinding.inflate(layoutInflater)
          override val mViewModel: TxnReceiptViewModel by viewModels()


          override fun onCreate(savedInstanceState: Bundle?) {
              super.onCreate(savedInstanceState)
              setContentView(binding.root)
              val bundle = intent.extras

              paymentId = bundle?.getString( com.paulmerchants.gold.utility.Constants.PAYMENT_ID)

              paymentId?.let {
                  mViewModel.getPaidReceiptNew(
                      it
                  )
              }

              binding.apply {

                  gotoHomeBtn.setOnClickListener {
                      val intent = Intent(this@PaymentConfirmed, MainActivity::class.java)
                      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                      intent.putExtra(com.paulmerchants.gold.utility.Constants.GO_TO_HOME, true) // Pass a flag to navigate to HomeFragment
                      startActivity(intent)
                      finish()
//
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
                          this@PaymentConfirmed,
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
                             this@PaymentConfirmed, R.anim.slide_up
                          ))


                      paymentConfirmedParent.show()

                      paymentConfirmedParent.clearAnimation()
                  }

              }
              mViewModel.paidReceiptNew.observe(this) {
                  it?.let {
                      setData(it)
                  }
              }
          }








   // 82233213123
    private fun setData(it: RespPayReceiptNew) {
        binding.apply {
            paymentConfirmIv.setImageResource(
                if (it.data?.paymentDetailsDTO?.captured == true) {
                    R.drawable.pay_confirm_tick_icon
                } else {
                    R.drawable.baseline_error
                }
            )
            paymetConfirmTv.text =
                if (it.data?.paymentDetailsDTO?.captured == true) {
                   "PAYMENT CONFIRMED!!"
                } else {
                  "PAYMENT FAILED!!"
                }

            transIdTv.text = it.data?.paymentDetailsDTO?.id?: "NA"
            accountNoTv.text =it.data?.accNo?: "NA"
            customerNameTv.text =  AppSharedPref.getStringValue(
                com.paulmerchants.gold.utility.Constants.CUSTOMER_NAME,
            )?.substringBefore(" ")?: "NA"
//            transDteAndTimeTv.text = it.data.entityPayment?.created_at
            transDteAndTimeTv.text = it.data?.paymentDetailsDTO?.created_at?.let { it1 ->
                AppUtility.getDate (
                    it1
                )
            }?: "NA"
            modeOfPaymentTv.text = it.data?.paymentDetailsDTO?.method?: "NA"
            amountPaidTv.text =  "${getString(R.string.Rs)} ${it.data?.paymentDetailsDTO?.amount?.let { it1 ->
                AppUtility.getTwoDigitAfterDecimal(
                    it1.toDouble())
            }?: "NA"}"


        }
    }
}