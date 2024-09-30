package com.paulmerchants.gold.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseActivity
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.PaymentsModeNewBinding
import com.paulmerchants.gold.location.LocationProvider
import com.paulmerchants.gold.model.newmodel.Notes
import com.paulmerchants.gold.model.newmodel.PayAllnOneGoDataTobeSent
import com.paulmerchants.gold.model.newmodel.ReqCreateOrder
import com.paulmerchants.gold.model.newmodel.RespCustomCustomerDetail
import com.paulmerchants.gold.model.newmodel.StatusPayment
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showCustomDialogForRenewCard
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.utility.showCustomDialogFoPaymentStatus
import com.paulmerchants.gold.viewmodels.PaymentViewModel
import com.razorpay.PaymentData
import com.razorpay.PaymentMethodsCallback
import com.razorpay.PaymentResultWithDataListener
import com.razorpay.Razorpay
import com.razorpay.ValidationListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PaymentActivity : BaseActivity<PaymentViewModel, PaymentsModeNewBinding>() {
    private var isReadyForPayment: Boolean = false
    private var payAlllInOneGo: PayAllnOneGoDataTobeSent? = null
    private var amountToPay: Double? = 0.0
    private var customerAcc: String? = null
    private var isCustomPay: Boolean? = false
    private var payload = JSONObject()
    private var razorpay: Razorpay? = null
    private lateinit var banksListAdapter: ArrayAdapter<String>
    private lateinit var walletListAdapter: ArrayAdapter<String>
    private lateinit var bankDialog: AlertDialog
    private lateinit var walletDialog: AlertDialog
    private val paymentViewModel: PaymentViewModel by viewModels()
    private var respCustomerDetail: RespCustomCustomerDetail? = null
    private var isDown: Boolean = false

    override fun getViewBinding() = PaymentsModeNewBinding.inflate(layoutInflater)
    override val mViewModel: PaymentViewModel by viewModels()
    lateinit var locationProvider: LocationProvider
    var mLocation: Location? = null
//    var isUpiIntent = false
//    var isUpiCollect = false

    enum class PaymentMode {
        UPI_COLLECT, UPI_INTENT, DebitCard, Netbanking, CreditCard, Wallet
    }

    companion object {
        lateinit var context: WeakReference<Context>
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        context = WeakReference(this)


        AppUtility.changeStatusBarWithReqdColor(this, R.color.pg_color)
        val bundle = intent.extras
        amountToPay = bundle?.getDouble(Constants.AMOUNT_PAYABLE, 0.0)
        customerAcc = bundle?.getString(Constants.CUST_ACC)
        isCustomPay = bundle?.getBoolean(Constants.IS_CUSTOM_AMOUNT, false)
        locationProvider = LocationProvider(this, object : LocationProvider.LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.e(
                    TAG,
                    "onLocationChanged: .....${location.latitude}-----${location.longitude}",
                )
                mLocation = location
            }

        }, this)
        locationProvider.startLocationUpdates()

        payAlllInOneGo =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) bundle?.getParcelable(
                Constants.PAY_ALL_IN_GO_DATA, PayAllnOneGoDataTobeSent::class.java
            ) else intent?.getParcelableExtra(Constants.PAY_ALL_IN_GO_DATA)
        Log.e(
            "TAGGGGGGG",
            "initialize: -customerAcc-----$customerAcc------$payAlllInOneGo------$amountToPay"
        )
        modifyHeaders()

        if (amountToPay != 0.0) {
            binding.amountPaidTv.text = "${getString(R.string.Rs)}$amountToPay"
        } else {
            binding.amountPaidTv.text = "${getString(R.string.Rs)}${payAlllInOneGo?.amount ?: 0}"
            amountToPay = payAlllInOneGo?.amount
        }
        var bhmValue = true
//        var walletValue = true
        var creditValue = true
        var netBanking = true
        binding.underMainParent.closeBtn.setOnClickListener {
            finish()
            MainActivity().finish()
        }
        paymentViewModel.checkForDownFromRemoteConfig()
        paymentViewModel.isRemoteConfigCheck.observe(this) {
            it?.let {
                if (it) {
                    binding.underMainParent.root.show()
                    binding.clOuter.hide()
                    isDown = it
                } else {
//                    if(!BuildConfig.DEBUG) {
                    paymentViewModel.getUnderMaintenanceStatusCheck()
//                    }
                }
            }
        }

        paymentViewModel.isUnderMainLiveData.observe(this) {
            it?.let {
                if (it.statusCode == "200") {
                    if (it.data.down && it.data.id == 1) {
                        binding.underMainParent.root.show()
                        binding.clOuter.hide()
                        isDown = it.data.down
                    } else if (it.data.down && it.data.id == 2) {
                        binding.clOuter.hide()

                        it.data.endTime?.let { endTime ->
                            showUnderMainTainTimerPage(endTime)
                        }

                        isDown = it.data.down
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        // Kotlin code to navigate from PaymentActivity to MainActivity


                    } else {
                        binding.clOuter.show()
                        binding.underMainTimerParent.root.hide()
                        paymentViewModel.getCustomerDetails(AppSharedPref, mLocation)
                        paymentViewModel.getPaymentMethod(AppSharedPref)
                    }
                }
            }
        }

        binding.payDebitCredit.setOnClickListener {
            if (isValidate()) {
                initRazorpay()
                payWithDebitCard()
                amountToPay?.let { it1 -> createOrder(it1, notes = "paying from debit_or_credit") }
            }
        }

        paymentViewModel.getPaymentMethod.observe(this) {
            it?.let {
                for (paymentMethods in it.data) {
                    Log.e(TAG, "onCreate: .........$paymentMethods")
                    if (paymentMethods.method == PaymentMode.UPI_COLLECT.name) {
//                        val isUpiCollect =
                        if (paymentMethods.value) {
                            showUpiCollect()
                            Log.e(TAG, "onCreate: .........rere")
                        } else {
                            hideUPiCollect()
                            Log.e(TAG, "onCreate: ////////")
                        }

//                        if (paymentMethods.method == PaymentMode.UPI_INTENT.name) {
//                            val isUpiIntent = paymentMethods.value
//                            Log.e(TAG, "onCreate: .......$isUpiCollect-----$isUpiIntent")
//                            if (!isUpiCollect && !isUpiIntent) {
//                                binding.bhmUpiParent.hide()
//                            } else {
//                                Log.e(TAG, "onCreate: .........3333")
//                                if (isUpiCollect) {
//                                    showUpiCollect()
//                                } else {
//                                    hideUPiCollect()
//                                }
//                                if (isUpiIntent) {
//                                    binding.upiMethodParent.show()
//                                } else {
//                                    binding.upiMethodParent.hide()
//                                }
//                            }
//                        }
                    }
                    if (paymentMethods.method == PaymentMode.UPI_INTENT.name) {
//                        val isUpiIntent = paymentMethods.value
//                        Log.e(TAG, "onCreate: .......$isUpiCollect-----$isUpiIntent"
//                            if (!isUpiCollect && !isUpiIntent) {
//                                binding.bhmUpiParent.hide()
//                            } else {
                        Log.e(
                            TAG,
                            "onCreate: ..${paymentMethods.method}......${paymentMethods.value}",
                        )
                        if (paymentMethods.value) {
                            binding.upiMethodParent.show()
                        } else {
                            binding.upiMethodParent.hide()
                        }
//                            }
                    }
                    if (paymentMethods.method == PaymentMode.DebitCard.name) {
                        val isValueDebitCard = paymentMethods.value
                        if (paymentMethods.method == PaymentMode.CreditCard.name) {
                            val isValueForCreditCard = paymentMethods.value

                            if (isValueDebitCard && !isValueForCreditCard) { //10
                                binding.nbTv.text = "Debit Card"
                                binding.creditDebitParent.show()
                            } else if (isValueDebitCard && isValueForCreditCard) {//11
                                binding.nbTv.text = "Credit/Debit Card"
                                binding.creditDebitParent.show()
                            } else if (!isValueDebitCard && isValueForCreditCard) {//01
                                binding.nbTv.text = "Credit Card"
                                binding.creditDebitParent.show()
                            } else {//00
                                binding.creditDebitParent.hide()
                            }
                        }


                    }
                    if (paymentMethods.method == PaymentMode.Netbanking.name) {
                        if (paymentMethods.value) {
                            binding.netBankingParentParent.show()
                        } else {
                            binding.netBankingParentParent.hide()
                        }
                    }

                    if (paymentMethods.method == PaymentMode.Wallet.name) {
                        if (paymentMethods.value) {
                            binding.walletParent.show()
                        } else {
                            binding.walletParent.hide()
                        }
                    }
                }


                /*  when {
                      it.data.CreditCard && it.data.DebitCard && it.data.UPI && it.data.Wallet && it.data.Netbanking -> {

                      }
                      it.data.CreditCard && it.data.DebitCard && it.data.UPI && it.data.Netbanking -> {
                          binding.apply {
                              walletParent.hide()
                              creditDebitParent.show()
                              nbTv.text ="Credit/Debit Card"
                              bhmUpiParent.show()
                              netBankingParentParent.show()
                          }
                      }
                      it.data.CreditCard && it.data.DebitCard && it.data.UPI -> {
                          binding.apply {
                              walletParent.hide()
                              creditDebitParent.show()
                              nbTv.text ="Credit/Debit Card"
                              bhmUpiParent.show()
                              netBankingParentParent.show()
                          }
                      }
                      it.data.CreditCard -> {

                      }
                      else -> {

                      }
                  }*/
            }
        }
        paymentViewModel.getRespCustomersDetailsLiveData.observe(this) {
            it?.let {
                isReadyForPayment = true
                respCustomerDetail = it
            }
        }
        paymentViewModel.respPaymentUpdate.observe(this) {
            it?.let {
                Log.d(TAG, "ojnnnnnn: /.................${it.status}")

                if (it.statusCode == "200") {
                    Log.d(TAG, "ojnnnnnn: /.................$it")
                    val bundle = Bundle().apply {
                        putString(
                            com.paulmerchants.gold.utility.Constants.PAYMENT_ID,
                            it.data.paymentId
                        )


                    }

                    showCustomDialogFoPaymentStatus(
                        message = "${it.message}\n Payment has been collected. It will be reflected in you loan account in a few minutes.",
                        isClick = {

                        })
//                    val bundle = Bundle().apply {
//                        putString(
//                            com.paulmerchants.gold.utility.Constants.PAYMENT_ID,
//                            it.data.paymentId
//                        )
//
//                    }
//                navController.navigateUp()
//                    navController.popBackStack(R.id.paymentModesFragNew, true)
//                    navController.navigate(R.id.paidReceiptFrag, bundle)
                    /* val paymentStatus = Bundle().apply {
                         putParcelable(Constants.PAYMENT_STATUS,it)
                     }*/
//                    findNavController().navigate(R.id.transactionDoneScreenFrag)

//                    (activity as MainActivity).commonViewModel.getPendingInterestDues((activity as MainActivity)?.appSharedPref)
                } else {
//                    val bundle = Bundle().apply {
//                        putString(
//                            com.paulmerchants.gold.utility.Constants.PAYMENT_ID,
//                            it.data.paymentId
//                        )
//
//
//                    }
//                    val intent = Intent(this, MainActivity::class.java)
//                    intent.putExtras(bundle)
//                    startActivity(intent)
//                    finish()
//                    val bundle = Bundle().apply {
//                        putString(
//                            com.paulmerchants.gold.utility.Constants.PAYMENT_ID,
//                            it.data.paymentId
//                        )
//
//                    }
//                    navController.navigateUp()
//                    navController.popBackStack(R.id.paymentModesFragNew, true)
//                    navController.navigate(R.id.paidReceiptFrag, bundle)
                    showCustomDialogFoPaymentStatus(message = it.message, isClick = {

                    })
                }
            }
        }


        binding.enterCardNumEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for formatting
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentText = s.toString().replace("\\s".toRegex(), "")
                val formattedText = buildString {
                    currentText.chunked(4).forEachIndexed { index, chunk ->
                        if (index != 0) append(" ")
                        append(chunk)
                    }
                }
                if (s.toString() != formattedText) {
                    binding.enterCardNumEt.setText(formattedText)
                    binding.enterCardNumEt.setSelection(formattedText.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed for formatting
            }
        })
        binding.enterExpireDateEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {/*
                                if (s?.length == 2 && before == 0) { // Assuming MM/YYYY format
                                    binding.enterExpireDateEt.setText(String.format("%s/", s))
                                    binding.enterExpireDateEt.setSelection(binding.enterExpireDateEt.text?.length ?: 0)
                                }*/

                val input = s?.toString()?.replace("\\s".toRegex(), "") ?: ""

                if (input.length <= 4) {
                    val monthPart = input.take(2).toIntOrNull() ?: 0
//                    val yearPart = input.takeLast(2).toIntOrNull() ?: 0

                    val isValidMonth = monthPart in 1..12
//                    val isValidYear = yearPart in 24..40
                    val formattedText = when {
                        input.length == 3 && input[2] != '/' -> "${input.take(2)}/${input.drop(2)}"
                        input.length == 1 && monthPart in 2..9 -> "0$input/"
                        input.length == 2 && monthPart > 12 -> "1"
                        else -> input
                    }
                    if (input != formattedText) {
                        binding.enterExpireDateEt.setText(formattedText)
                        binding.enterExpireDateEt.setSelection(formattedText.length)
                    }
                    if (!(isValidMonth)) {
                        binding.enterExpireDateEt.error = "Invalid expiry date"
                    }
                }

            }
        })
        paymentViewModel.responseCreateOrder.observe(this) {
            it?.let {
                Log.d(TAG, "onCreate: ...................$it")
                if (it.statusCode == "200") {
                    when {
                        bhmValue -> {
                            Log.e("TAG", "onStart: ---bhmValue")
                        }

                        /*    walletValue -> {
                                Log.e("TAG", "onStart:---walletValue ")

                            }*/

                        creditValue -> {
                            Log.e("TAG", "onStart: --creditValue")

                        }

                        netBanking -> {
                            Log.e("TAG", "onStart: -netBanking")

                        }
                    }
                    payload.put("order_id", it.data.orderId)
                    try {
                        sendRequest()
                    } catch (e: java.lang.Exception) {
                        Log.d("TAG", "onResponse: ....${e.message}")
                    }

                }
            }
        }
        binding.spinnerNetBanking.setOnClickListener {
            if (this::bankDialog.isInitialized) bankDialog.show()
        }
        binding.spinnerWallet.setOnClickListener {
            if (this::walletDialog.isInitialized) walletDialog.show()
        }
        binding.PayNetBanking.setOnClickListener {
            amountToPay?.let { it1 -> createOrder(it1, notes = "paying from netbanking") }
        }
        binding.PayWallet.setOnClickListener {
            amountToPay?.let { it1 -> createOrder(it1, notes = "paying from wallet") }
        }

        binding.gPayTv.setOnClickListener {
            if (isReadyForPayment) {
                initRazorpay()
                upiIntentGooglePay()
                amountToPay?.let { it1 -> createOrder(it1, notes = "paying from g_pay_intent") }
            } else {
                "Please Wait...".showSnackBar()
            }
        }
        binding.phonePeTv.setOnClickListener {
            if (isReadyForPayment) {
                initRazorpay()
                upiIntentPhonePe()
                amountToPay?.let { it1 -> createOrder(it1, notes = "paying from phone_pay_intent") }
            } else {
                "Please Wait...".showSnackBar()
            }
        }
        binding.paytmTv.setOnClickListener {
            if (isReadyForPayment) {
                initRazorpay()
                upiIntentPaytm()
                amountToPay?.let { it1 -> createOrder(it1, notes = "paying from paytm _intent") }
            } else {
                "Please Wait...".showSnackBar()
            }
        }
        binding.otherApps.setOnClickListener {
            if (isReadyForPayment) {
                initRazorpay()
                otherIntent()
                amountToPay?.let { it1 -> createOrder(it1, notes = "paying from other_app") }
            } else {
                "Please Wait...".showSnackBar()
            }
        }
        binding.apply {
            verifyUpiBtn.setOnClickListener {
                if (binding.enterUpiEt.text.toString().isNotEmpty()) {
                    if (AppUtility.validateUPI(binding.enterUpiEt.text.toString())) {

                        Log.d(TAG, "onCreate: validate upi")
                        initRazorpay()
                        upiCollect(binding.enterUpiEt.text.toString())
                        amountToPay?.let { it1 ->
                            createOrder(
                                it1, notes = "paying from upi_collect"
                            )
                        }
                    } else {
                        "Please enter valid UPI Id".showSnackBar()
                    }
                }
            }
            upiTv.setOnClickListener {
                Log.d(TAG, "onCreate: bhmValue && isReadyForPayment  false")
                if (bhmValue && isReadyForPayment) {
                    Log.d(TAG, "onCreate: bhmValue && isReadyForPayment")
                    bhmUpiParent.setBackgroundResource(R.drawable.rect_opem_loans)
                    arrowDowmBhmIv.setImageResource(R.drawable.cross_icon)
                    upiMethodParent.show()


//                        showCustomDialogOTPVerify(
//                            requireContext(),
//                            "OTP send to the number ending with *4555"
//                        )
//                        addNewUpiTv.hide()
//                        selectUpiIdParent.show()
//                        upiCardTv.show()
//                        upiNumTv.show()
//                        upiMethodParent.hide()
//                        arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
//                        bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    }


//                    upiMethodParent.startAnimation(
//                        AnimationUtils.loadAnimation(
//                            requireContext(), R.anim.slide_down
//                        )
//                    )
                    //wallet
                    walletMethodParent.hide()
                    walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
                    //credit
                    creditCardParent.hide()
                    creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)

                    //netBanking
                    netBankCardParent.hide()
                    netBankingParentParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDownNbIv.setImageResource(R.drawable.arrow_down_black)

                    bhmValue = false
                } else {
                    upiMethodParent.hide()
//                    upiMethodParent.startAnimation(
//                        AnimationUtils.loadAnimation(
//                            requireContext(), R.anim.slide_up
//                        )
//                    )
                    arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
                    bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    bhmValue = true
                }

            }/*
                        walletParent.setOnClickListener {
                            if (walletValue) {
                                arrowDownWalletIv.setImageResource(R.drawable.cross_icon)
                                walletMethodParent.show()
                                walletParent.setBackgroundResource(R.drawable.rect_opem_loans)

                                // upi
                                upiMethodParent.hide()
                                bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                                arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
                                //creditCa0rd
                                creditCardParent.hide()
                                creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                                arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)

                                //netBanking
                                netBankCardParent.hide()
                                netBankingParentParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                                arrowDownNbIv.setImageResource(R.drawable.arrow_down_black)
                                walletValue = false

                            } else {
                                walletMethodParent.hide()
                                walletMethodParent.startAnimation(
                                    AnimationUtils.loadAnimation(
                                        this@PaymentActivity, R.anim.slide_up
                                    )
                                )
                                arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
                                walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                                walletValue = true
                            }
                        }
            */
            nbTv.setOnClickListener {
                if (creditValue && isReadyForPayment) {
                    nbTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross_icon, 0)
//                    arrowDownCreditIv.setImageResource(R.drawable.cross_icon)
                    creditDebitParent.setBackgroundResource(R.drawable.rect_opem_loans)
                    creditCardParent.show()
                    //wallet
                    walletMethodParent.hide()
                    walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
                    //upi
                    upiMethodParent.hide()
                    bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
                    //netBanking
                    netBankCardParent.hide()
                    netBankingParentParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDownNbIv.setImageResource(R.drawable.arrow_down_black)
                    creditValue = false
                } else {
                    creditCardParent.hide()
                    creditCardParent.startAnimation(
                        AnimationUtils.loadAnimation(
                            this@PaymentActivity, R.anim.slide_up
                        )
                    )
//                    arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)
                    nbTv.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.arrow_down_black, 0
                    )
                    creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    creditValue = true
                }

            }
            creditTv.setOnClickListener {
                initRazorpay()
                if (netBanking && isReadyForPayment) {
                    arrowDownNbIv.setImageResource(R.drawable.cross_icon)
                    netBankingParentParent.setBackgroundResource(R.drawable.rect_opem_loans)
                    netBankCardParent.show()
                    //wallet
                    walletMethodParent.hide()
                    walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
                    //upi
                    upiMethodParent.hide()
                    bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)

                    //creditCa0rd
                    creditCardParent.hide()
                    creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)

                    netBanking = false
                } else {
                    netBankCardParent.hide()
                    netBankCardParent.startAnimation(
                        AnimationUtils.loadAnimation(
                            this@PaymentActivity, R.anim.slide_up
                        )
                    )
                    arrowDownNbIv.setImageResource(R.drawable.arrow_down_black)
                    netBankingParentParent.setBackgroundResource(R.drawable.card_sky_rect_6)

                    netBanking = true
                }
            }
        }


    }

    private fun showUpiCollect() {
        binding.apply {
            addUpiTv.show()
            enterUpiEt.show()
            verifyUpiBtn.show()
        }
    }

    private fun hideUPiCollect() {
        binding.apply {
            addUpiTv.hide()
            enterUpiEt.hide()
            verifyUpiBtn.hide()
        }
    }

    private fun toggleWebViewVisibility(webviewVisibility: Int) {
        binding.parentWeb.visibility = webviewVisibility
        binding.webview.visibility = webviewVisibility
        binding.clOuter.visibility = if (webviewVisibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun isValidate(): Boolean {
        return when {
            binding.enterNameOnCardEt.text?.isEmpty() == true && binding.enterCardNumEt.text?.isEmpty() == true && binding.enterExpireDateEt.text?.isEmpty() == true && binding.enterCvvEt.text?.isEmpty() == true -> {
                "Please fill all card details".showSnackBar()
                false
            }

            binding.enterNameOnCardEt.text?.isEmpty() == true -> {
                "Please enter card holder name".showSnackBar()
                false
            }

            binding.enterCardNumEt.text?.isEmpty() == true -> {
                "Please enter card number".showSnackBar()
                false
            }

            binding.enterCardNumEt.text?.length != 19 -> {
                "Please enter valid card number".showSnackBar()
                false
            }

            binding.enterExpireDateEt.text?.isEmpty() == true -> {
                "Please enter expiry month-date".showSnackBar()
                false
            }

            binding.enterExpireDateEt.text?.length != 5 -> {
                "Please enter valid expiry date in format".showSnackBar()
                false
            }

            binding.enterCvvEt.text?.isEmpty() == true -> {
                "Please enter CVV".showSnackBar()
                false
            }

            else -> true
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun createOrder(amount: Double, notes: String) {
        Log.d("TAG", "createOrder: ......$amount")

        /*  if (BuildConfig.DEBUG && mLocation?.isMock == true) {
              "Please disable your mock Location from developer option".showSnackBar()
              return
          }*/
        if (!isLocationEnabled()) {
            buildAlertMessageNoGps()
        } else {
            Log.d(TAG, "createOrder: ....api_Calls")
//            if (!BuildConfig.DEBUG) {
            paymentViewModel.getUnderMaintenanceStatus(
                reqCreateOrder = ReqCreateOrder(
                    amount = amount,
                    currency = "INR",
                    custId = AppSharedPref.getStringValue(com.paulmerchants.gold.utility.Constants.CUSTOMER_ID)
                        .toString(),
                    notes = Notes(
                        "$notes custId=${AppSharedPref.getStringValue(com.paulmerchants.gold.utility.Constants.CUSTOMER_ID)}",
                        "Loan Acc Number: $customerAcc"
                    ),
                    receipt = "${AppUtility.getCurrentDate()}_${BuildConfig.VERSION_NAME}",
                    accNo = customerAcc.toString(),
                    makerId = "12545as",
                    submit = true,
                    macId = Build.ID,
                    valueDate = AppUtility.getCurrentDate()
                ), mLocation
            )
//            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your Location seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, _ ->
                dialog.dismiss()
                finish()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(
                "No"
            ) { dialog, _ ->
                finish()
                dialog.cancel()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun sendRequest() {
        Log.d("TAG", "sendRequest: .......$payload")
        razorpay?.validateFields(payload, object : ValidationListener {
            override fun onValidationSuccess() {
                Log.e(TAG, "onValidationSuccess: /.......//.")
                toggleWebViewVisibility(View.VISIBLE)
                razorpay?.submit(payload, object : PaymentResultWithDataListener {
                    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
                        //Razorpay payment ID and PaymentData passed here after a successful payment
                        Log.i(TAG, "onPaymentSuccess: ///////////.....${p1?.paymentId}")
                        toggleWebViewVisibility(View.GONE)
                        if (p1?.paymentId != null) {
                            "Payment Success".showSnackBar()
                            if (payAlllInOneGo != null) {
                                updatePaymentStatusToServerToAllInOneGo(
                                    StatusPayment("captured", p1)
                                )
                            } else {
                                updatePaymentStatusToServer(
                                    StatusPayment("captured", p1), false
                                    //                                                isCustomPay ?: false
                                )

                            }
                        } else {
                            "Unable to initiate the payment\nplease try again later".showSnackBar()
                        }
                    }

                    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
                        // Error code and description is passed here
                        "Payment Failed".showSnackBar()
                        Log.i(TAG, "onPaymentError: ----${p2?.paymentId}")
                        toggleWebViewVisibility(View.GONE)
//                        if (p2?.paymentId != null) {
                        p2.let {
                            if (payAlllInOneGo != null) {

                                updatePaymentStatusToServerToAllInOneGo(
                                    StatusPayment("not_captured", p2)
                                )

                            } else {
                                updatePaymentStatusToServer(
                                    StatusPayment("not_captured", p2), false
//                                                isCustomPay ?: false
                                )
                            }
//                            }

                        }
//                        else {
//                            "Payment failed.Please try again.".showSnackBar()
//                        }

                    }
                })
            }

            override fun onValidationError(p0: MutableMap<String, String>?) {
                Log.e(TAG, "onValidationError: ..........$p0")
            }
        })

    }


    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause:......")
        paymentViewModel.isCalled = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationProvider.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                locationProvider.startLocationUpdates()
            } else {
                Log.e(TAG, "onRequestPermissionsResult: ............no permission....")
                showCustomDialogFoPaymentStatus(
                    header = "Location Access Required",
                    message = "Please enable location access in your device settings to finalize payments.",
                    isClick = {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    })
//                locationProvider.startLocationUpdates()
                // Permission denied, handle accordingly
                // Display a message informing the user about the necessity of location permission
                // Encourage them to grant the permission or provide alternative functionality
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (razorpay != null) {
            razorpay?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updatePaymentStatusToServer(
        statusData: StatusPayment,
        isCustom: Boolean,
    ) {
        Log.d(TAG, "updatePaymentStatusToServer: $amountToPay....$statusData")
        if (customerAcc != null && amountToPay != null) {
            amountToPay?.let {
                paymentViewModel.updatePaymentStatus(
                    this,
                    status = statusData.status,
                    razorpay_payment_id = statusData.paymentData?.paymentId ?: "",
                    razorpay_order_id = statusData.paymentData?.orderId ?: "",
                    razorpay_signature = statusData.paymentData?.signature ?: "",
                    custId = AppSharedPref.getStringValue(com.paulmerchants.gold.utility.Constants.CUSTOMER_ID)
                        .toString(),
                    amount = amountToPay,
                    contactCount = 0,
                    description = "desc_payment",
                    account = customerAcc.toString(),
                    isCustom = isCustom, location = mLocation
                )
            }
        } else {
            "Amount: Some thing went wrong".showSnackBar()
        }
    }

    private fun updatePaymentStatusToServerToAllInOneGo(
        statusData: StatusPayment,
    ) {
        Log.d(TAG, "updatePaymentStatusToServer: $amountToPay....$statusData")
        if (amountToPay != null) {
            amountToPay?.let {
                payAlllInOneGo?.payAll?.let { payAllGo ->
                    paymentViewModel.updatePaymentStatusAllInOneGo(
                        status = statusData.status,
                        razorpay_payment_id = statusData.paymentData?.paymentId.toString(),
                        razorpay_order_id = statusData.paymentData?.orderId.toString(),
                        razorpay_signature = statusData.paymentData?.signature.toString(),
                        custId = AppSharedPref.getStringValue(com.paulmerchants.gold.utility.Constants.CUSTOMER_ID)
                            .toString(),
                        amount = amountToPay,
                        contactCount = 0,
                        description = "desc_payment",
                        listOfPaullINOneGo = payAllGo, location = mLocation
                    )
                }
            }
        } else {
            "Amount: Some thing went wrong".showSnackBar()
        }
    }

    private fun initRazorpay() {
        Log.e(TAG, "initRazorpay: .")
        razorpay = Razorpay(this, BuildConfig.RAZORPAY_KEY)
        razorpay?.getPaymentMethods(object : PaymentMethodsCallback {
            override fun onPaymentMethodsReceived(p0: String?) {
                Log.e(TAG, "onPaymentMethodsReceived: ........$p0")
                val bankDialogBuilder = AlertDialog.Builder(this@PaymentActivity)
                val walletDialogBuilder = AlertDialog.Builder(this@PaymentActivity)
                val bankListLayout = LayoutInflater.from(this@PaymentActivity)
                    .inflate(R.layout.banks_list, null, false)
                val bankListView = bankListLayout.findViewById<ListView>(R.id.list_view_bank)
                val walletListLayout = LayoutInflater.from(this@PaymentActivity)
                    .inflate(R.layout.banks_list, null, false)
                val walletListView = walletListLayout.findViewById<ListView>(R.id.list_view_bank)
                p0?.let {
                    val bankKeys = ArrayList<String>()
                    val bankNames = ArrayList<String>()
                    val bankListJson = JSONObject(it).getJSONObject("netbanking")
                    val itr: Iterator<String> = bankListJson.keys()
                    while (itr.hasNext()) {
                        val key = itr.next()
                        bankKeys.add(key)
                        bankNames.add(bankListJson.getString(key))
                    }
//                    setUpBankSpinnerForRazorPay(bankNames, bankKeys)
                    banksListAdapter = ArrayAdapter(
                        this@PaymentActivity, android.R.layout.simple_list_item_1, bankNames
                    )
                    bankListView.adapter = banksListAdapter
                    bankDialogBuilder.setView(bankListLayout)
                    bankDialogBuilder.setTitle("Select a bank")
                    bankDialogBuilder.setPositiveButton(
                        "Ok"
                    ) { _, _ -> }
                    bankDialog = bankDialogBuilder.create()
                    bankListView.setOnItemClickListener { _, _, position, _ ->
                        basePayload()
                        payload.put("method", "netbanking")
                        payload.put("bank", bankKeys[position])
                        binding.spinnerNetBanking.text = bankNames[position]
                        bankDialog.dismiss()
                    }
                    val walletNames = ArrayList<String>()
                    val walletsListJson = JSONObject(it).getJSONObject("wallet")
                    val walletItr = walletsListJson.keys()
                    while (walletItr.hasNext()) {
                        val key = walletItr.next()
                        if (walletsListJson.getBoolean(key)) {
                            walletNames.add(key)
                        }
                    }
                    walletListAdapter = ArrayAdapter(
                        this@PaymentActivity, android.R.layout.simple_list_item_1, walletNames
                    )
                    walletListView.adapter = walletListAdapter
                    walletDialogBuilder.setView(walletListLayout)
                    walletDialogBuilder.setTitle("Select a Wallet")
                    walletDialogBuilder.setPositiveButton(
                        "Ok"
                    ) { _, _ -> }
                    walletDialog = walletDialogBuilder.create()
                    walletListView.setOnItemClickListener { _, _, position, _ ->
                        basePayload()
                        payload.put("method", "wallet")
                        payload.put("wallet", walletNames[position])
                        binding.spinnerWallet.text = walletNames[position]
                        walletDialog.dismiss()
                    }
                }
            }

            override fun onError(p0: String?) {
                Toast.makeText(
                    this@PaymentActivity, p0 ?: "Some thing went wrong..", Toast.LENGTH_LONG
                ).show()
            }
        })
        razorpay?.setWebView(binding.webview)
    }


    private fun upiCollect(vpa: String) {
        basePayload()
        payload.put("method", "upi")
        payload.put("vpa", vpa)
        payload.put("_[flow]", "collect")
    }


    private fun payWithDebitCard() {
        basePayload()

//        var cardNumberString = debitCardNumber?.text.toString()
//        cardNumberString = cardNumberString.replace("\\s".toRegex(), "")

        payload.put("method", "card")
        payload.put(
            "card[number]", binding.enterCardNumEt.text.toString().replace("\\s".toRegex(), "")
        ) //4111111111111111
        payload.put(
            "card[expiry_month]", binding.enterExpireDateEt.text.toString().substring(0, 2)
        )
        payload.put(
            "card[expiry_year]", binding.enterExpireDateEt.text.toString().takeLast(2)
        )
        payload.put("card[cvv]", binding.enterCvvEt.text.toString())
        payload.put("card[name]", binding.enterNameOnCardEt.text.toString())

    }

    override fun onBackPressed() {
        super.onBackPressed()
        showCustomDialogForRenewCard(onOkClick = {
            if (it) {
                if (binding.webview.visibility == View.VISIBLE) {
                    razorpay?.onBackPressed()
                    toggleWebViewVisibility(View.GONE)
                } else {
                    finish()
                }
            }
        })
    }

    private fun upiIntent() {
        basePayload()
        val jArray = JSONArray()
        jArray.put("in.org.npci.upiapp")
        jArray.put("com.snapwork.hdfc")
        payload.put("method", "upi")
        payload.put("_[flow]", "intent")
        payload.put("preferred_apps_order", jArray)
        payload.put("other_apps_order", jArray)
    }

    private fun upiIntentGooglePay() {
        basePayload()
//        val jArray = JSONArray()
//        jArray.put("in.org.npci.upiapp")
//        jArray.put("com.snapwork.hdfc")
        payload.put("method", "upi")
        payload.put("_[flow]", "intent")
        payload.put("upi_app_package_name", "com.google.android.apps.nbu.paisa.user")
//        payload.put("preferred_apps_order", jArray)
//        payload.put("other_apps_order", jArray)
    }

    private fun upiIntentPhonePe() {
        basePayload()
//        val jArray = JSONArray()
//        jArray.put("in.org.npci.upiapp")
//        jArray.put("com.snapwork.hdfc")
        payload.put("method", "upi")
        payload.put("_[flow]", "intent")
        payload.put("upi_app_package_name", "com.phonepe.app")
//        payload.put("preferred_apps_order", jArray)
//        payload.put("other_apps_order", jArray)
    }

    private fun upiIntentPaytm() {
        basePayload()
//        val jArray = JSONArray()
//        jArray.put("in.org.npci.upiapp")
//        jArray.put("com.snapwork.hdfc")
        payload.put("method", "upi")
        payload.put("_[flow]", "intent")
        payload.put("upi_app_package_name", "net.one97.paytm")
//        payload.put("preferred_apps_order", jArray)
//        payload.put("other_apps_order", jArray)
    }

    private fun otherIntent() {
        basePayload()
        val jArray = JSONArray()
        jArray.put("in.org.npci.upiapp")
        jArray.put("com.snapwork.hdfc")
        payload.put("method", "upi")
        payload.put("_[flow]", "intent")
        payload.put("preferred_apps_order", jArray)
        payload.put("other_apps_order", jArray)
    }

    private fun basePayload() {
        try {
            payload = JSONObject()
            payload.put("amount", amountToPay?.times(100.00)?.toInt())
            payload.put("currency", "INR")
            payload.put(
                "contact", respCustomerDetail?.respGetCustomer?.MobileNo
            )
            payload.put(
                "email", respCustomerDetail?.emailIdNew
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun modifyHeaders() {
        binding.apply {
            binding.headerBillMore.apply {
                backIv.setOnClickListener {
                    finish()
                }
                titlePageTv.text = getString(R.string.pay_modes)
                endIconIv.setImageResource(R.drawable.quest_circle)
                endIconIv.hide()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showUnderMainTainTimerPage(endTime: String) {

        binding.underMainTimerParent.root.show()
        startDailyCountdown(endTime)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun startDailyCountdown(endTime: String = "2024-09-20 14:40:30") {

        val endTimeFormat = AppUtility.getHourMinuteSecond(endTime)

        val targetTime = LocalTime.of(
            endTimeFormat?.first!!, endTimeFormat.second,
            endTimeFormat.third
        )

        // Get the current time (India Standard Time)
        val currentTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))

        // Get the time today when the countdown should end (today's 3:00:00 PM)
        var targetISTTime = currentTime.with(targetTime)

        // If the current time is already past 3:00 PM, set the target to tomorrow at 3:00 PM
        if (currentTime.isAfter(targetISTTime)) {
            targetISTTime = targetISTTime.plusDays(1)
        }

        // Calculate the difference between now and the target time in milliseconds
        val millisUntilTarget = ChronoUnit.MILLIS.between(currentTime, targetISTTime)

        // Start the countdown timer from now until the target time
        object : CountDownTimer(millisUntilTarget, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                // Calculate hours, minutes, and seconds remaining
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                // Update your TextView with the countdown time
                if (hours.toInt() == 0) {
                    binding.underMainTimerParent.timerTextView.text =
                        String.format("%02d:%02d", minutes, seconds)

                } else {
                    binding.underMainTimerParent.timerTextViewBg.text =
                        "88:88:88"
                    binding.underMainTimerParent.timerTextView.text =
                        String.format("%02d:%02d:%02d", hours, minutes, seconds)


                }

            }

            override fun onFinish() {
                // Reset or refresh your UI, or restart the countdown for the next day if needed
                binding.underMainTimerParent.timerTextView.text = "00:00"
                paymentViewModel.getUnderMaintenanceStatusCheck()

//                navController.clearBackStack(R.id.splashFragment)

            }
        }.start()
//        }
    }

    override fun onStop() {
        super.onStop()
        locationProvider.stopLocationUpdates()
    }


}