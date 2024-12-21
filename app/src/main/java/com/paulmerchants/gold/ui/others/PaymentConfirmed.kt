package com.paulmerchants.gold.ui.others

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseActivity
import com.paulmerchants.gold.databinding.LoanEmiPaymentConfirmedBinding
import com.paulmerchants.gold.model.responsemodels.RespPaymentReceipt

import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.CommonViewModel
import com.paulmerchants.gold.viewmodels.TxnReceiptViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PaymentConfirmed :
    BaseActivity<TxnReceiptViewModel, LoanEmiPaymentConfirmedBinding>() {
    var headerValue: String? = null
    var orderId: String? = null
    var paymentId: String? = null

    override fun getViewBinding() = LoanEmiPaymentConfirmedBinding.inflate(layoutInflater)
    override val mViewModel: TxnReceiptViewModel by viewModels()
    val commonViewModel: CommonViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val bundle = intent.extras

        orderId = bundle?.getString(com.paulmerchants.gold.utility.Constants.ORDER_ID)
        paymentId = bundle?.getString(com.paulmerchants.gold.utility.Constants.PAYMENT_ID)

        if (orderId?.isNotEmpty() == true) {
            orderId?.let {
                mViewModel.getPaidReceipt(
                    orderId = it, context = this
                )
            }
        } else {
            paymentId?.let {
                mViewModel.getPaidReceipt(
                    paymentId = it, context = this
                )
            }
        }
        binding.apply {

            gotoHomeBtn.setOnClickListener {
                val intent = Intent(this@PaymentConfirmed, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra(
                    com.paulmerchants.gold.utility.Constants.GO_TO_HOME,
                    true
                ) // Pass a flag to navigate to HomeFragment
                startActivity(intent)
                finish()
//
            }
            needHelpBtn.setOnClickListener {
                val phone = "18001371333"
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                startActivity(intent)
            }
            downLoadReceiptTv.setOnClickListener {

                val screenBitmap = AppUtility.getScreenBitmap(paymetRecieptcard)
                val pdfWidth = 500f
                val pdfHeight = 870f
                if (AppUtility.checkAndRequestPermissions(this@PaymentConfirmed)) {
                    AppUtility.saveAsPdf1(
                        this@PaymentConfirmed,
                        pdfWidth,
                        pdfHeight,
                        screenBitmap,
                        R.color.open_loans
                    )
                }

            }
            lifecycleScope.launch {
                delay(2000)
                loadingParent.hide()

                paymentConfirmedParent.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@PaymentConfirmed, R.anim.slide_up
                    )
                )


                paymentConfirmedParent.show()

                paymentConfirmedParent.clearAnimation()
            }

        }
        mViewModel.paidReceipt.observe(this) {
            it?.let {
                if (it.status_code == 200) {
                    setData(it.data)

                } else if (it.status_code == 498) {
                    commonViewModel.refreshToken(this)
                } else {
                    it.message.showSnackBar()
                }

            }
        }
        commonViewModel.refreshTokenLiveData.observe(this) {
            it?.let {
               if (it.status_code == 200) {
                   if (orderId?.isNotEmpty() == true){

                       orderId?.let { mViewModel.getPaidReceipt(orderId = it, context = this) }
                   }else{
                       paymentId?.let {
                           mViewModel.getPaidReceipt(paymentId = it, context = this)
                       }
                   }
               }
            }

        }
    }


    // 82233213123
    private fun setData(it: RespPaymentReceipt?) {
        binding.apply {
            paymentConfirmIv.setImageResource(
                if (it?.payment_details_dto?.captured == true) {
                    R.drawable.pay_confirm_tick_icon
                } else {
                    R.drawable.baseline_error
                }
            )
            paymetConfirmTv.text =
                if (it?.payment_details_dto?.captured == true) {
                    "PAYMENT CONFIRMED!!"
                } else {
                    "PAYMENT FAILED!!"
                }

            transIdTv.text =
                it?.payment_details_dto?.id ?: it?.payment_details_dto?.order_id ?: "NA"
            accountNoTv.text = it?.acc_no ?: "NA"
            customerNameTv.text = AppSharedPref.getStringValue(
                com.paulmerchants.gold.utility.Constants.CUSTOMER_NAME,
            )?.substringBefore(" ") ?: "NA"
//            transDteAndTimeTv.text = it.data.entityPayment?.created_at
            transDteAndTimeTv.text = it?.payment_details_dto?.created_at?.let { it1 ->
                AppUtility.getDate(
                    it1
                )
            } ?: "NA"
            modeOfPaymentTv.text = it?.payment_details_dto?.method ?: "NA"
            amountPaidTv.text = "${getString(R.string.Rs)} ${
                it?.payment_details_dto?.amount?.let { it1 ->
                    AppUtility.getTwoDigitAfterDecimal(
                        it1.toDouble()
                    )
                } ?: "NA"
            }"


        }
    }
}