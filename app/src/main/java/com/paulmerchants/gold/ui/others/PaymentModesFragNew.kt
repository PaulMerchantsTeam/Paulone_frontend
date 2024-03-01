package com.paulmerchants.gold.ui.others

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.PaymentsModeNewBinding
import com.paulmerchants.gold.model.newmodel.Notes
import com.paulmerchants.gold.model.newmodel.PayAllnOneGoDataTobeSent
import com.paulmerchants.gold.model.newmodel.ReqCreateOrder
import com.paulmerchants.gold.model.newmodel.RespCustomCustomerDetail
import com.paulmerchants.gold.model.newmodel.StatusPayment
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.ui.TAG
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants.PAYMENT_ID
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.PaymentViewModel
import com.razorpay.PaymentData
import com.razorpay.PaymentMethodsCallback
import com.razorpay.PaymentResultListener
import com.razorpay.PaymentResultWithDataListener
import com.razorpay.Razorpay
import com.razorpay.ValidationListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject


/**
 * https://razorpay.com/docs/payments/payment-methods/upi/google-pay/custom-integration/
 * https://razorpay.com/docs/payments/payment-methods/upi/supported-apps?search-string=PhonePe
 */


@AndroidEntryPoint
class PaymentModesFragNew : BaseFragment<PaymentsModeNewBinding>(PaymentsModeNewBinding::inflate) {
//    private var payAlllInOneGo: PayAllnOneGoDataTobeSent? = null
//    private var amountToPay: Double? = 0.0
//    private var customerAcc: String? = null
//    private var isCustomPay: Boolean? = false
//    private var payload = JSONObject()
//    private var razorpay: Razorpay? = null
//    private lateinit var banksListAdapter: ArrayAdapter<String>
//    private lateinit var walletListAdapter: ArrayAdapter<String>
//    private lateinit var bankDialog: AlertDialog
//    private lateinit var walletDialog: AlertDialog
//    private val paymentViewModel: PaymentViewModel by viewModels()
//    private var respCustomerDetail: RespCustomCustomerDetail? = null

    override fun PaymentsModeNewBinding.initialize() {
//        amountToPay = arguments?.getDouble(Constants.AMOUNT_PAYABLE)
//        customerAcc = arguments?.getString(Constants.CUST_ACC)
//        isCustomPay = arguments?.getBoolean(Constants.IS_CUSTOM_AMOUNT)
//        payAlllInOneGo =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(
//                Constants.PAY_ALL_IN_GO_DATA, PayAllnOneGoDataTobeSent::class.java
//            ) else arguments?.getParcelable<PayAllnOneGoDataTobeSent>(Constants.PAY_ALL_IN_GO_DATA) as PayAllnOneGoDataTobeSent?
//        Log.e("TAGGGGGGG", "initialize: -customerAcc-----------$payAlllInOneGo------$amountToPay")
//        modifyHeaders()
        /*binding.enterExpireDateEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
                if (start == 1 && start+added == 2 && p0?.contains('/') == false) {
                    binding.enterExpireDateEt.setText("$p0/")
                    Toast.makeText(context, "${binding.enterExpireDateEt.text}......11", Toast.LENGTH_SHORT).show()

                } else if (start == 3 && start-removed == 2 && p0?.contains('/') == true) {
                    binding.enterExpireDateEt.setText(p0.toString().replace("/", ""))
                    Toast.makeText(context, "${binding.enterExpireDateEt.text}....2", Toast.LENGTH_SHORT).show()
                }
            }
        })*/
    }

    override fun onStart() {
        super.onStart()

//        if (amountToPay != 0.0) {
//            binding.amountPaidTv.text = "${getString(R.string.Rs)}$amountToPay"
//        } else {
//            binding.amountPaidTv.text = "${getString(R.string.Rs)}${payAlllInOneGo?.amount ?: 0}"
//            amountToPay = payAlllInOneGo?.amount
//        }
//        var bhmValue = true
//        var walletValue = true
//        var creditValue = true
//        var netBanking = true
//        if (paymentViewModel.isCalled) {
//            (activity as MainActivity).appSharedPref?.let { paymentViewModel.getCustomerDetails(it) }
//            initRazorpay()
//            paymentViewModel.getPaymentMethod((activity as MainActivity).appSharedPref)
//        }
//        paymentViewModel.getPaymentMethod.observe(viewLifecycleOwner) {
//            it?.let {
//                if (it.data.DebitCard && !it.data.CreditCard) { //10
//                    binding.nbTv.text = "Debit Card"
//                    binding.creditDebitParent.show()
//                } else if (it.data.DebitCard && it.data.CreditCard) {//11
//                    binding.nbTv.text = "Credit/Debit Card"
//                    binding.creditDebitParent.show()
//                } else if (!it.data.DebitCard && it.data.CreditCard) {//01
//                    binding.nbTv.text = "Credit Card"
//                    binding.creditDebitParent.show()
//                } else {//00
//                    binding.creditDebitParent.hide()
//                }
//                if (it.data.Netbanking) {
//                    binding.netBankingParentParent.show()
//                } else {
//                    binding.netBankingParentParent.hide()
//
//                }
//                if (it.data.Wallet) {
//                    binding.walletParent.show()
//                } else {
//                    binding.walletParent.hide()
//                }
//                if (it.data.UPI) {
//                    binding.bhmUpiParent.show()
//                } else {
//                    binding.bhmUpiParent.show()
//                }
//                /*  when {
//                      it.data.CreditCard && it.data.DebitCard && it.data.UPI && it.data.Wallet && it.data.Netbanking -> {
//
//                      }
//                      it.data.CreditCard && it.data.DebitCard && it.data.UPI && it.data.Netbanking -> {
//                          binding.apply {
//                              walletParent.hide()
//                              creditDebitParent.show()
//                              nbTv.text ="Credit/Debit Card"
//                              bhmUpiParent.show()
//                              netBankingParentParent.show()
//                          }
//                      }
//                      it.data.CreditCard && it.data.DebitCard && it.data.UPI -> {
//                          binding.apply {
//                              walletParent.hide()
//                              creditDebitParent.show()
//                              nbTv.text ="Credit/Debit Card"
//                              bhmUpiParent.show()
//                              netBankingParentParent.show()
//                          }
//                      }
//                      it.data.CreditCard -> {
//
//                      }
//                      else -> {
//
//                      }
//                  }*/
//            }
//        }
//        paymentViewModel.respPaymentUpdate.observe(viewLifecycleOwner) {
//            it?.let {
//                Log.d(TAG, "ojnnnnnn: /.................${it.status}")
//
//                if (it.status == "200") {
//                    Log.d(TAG, "ojnnnnnn: /.................$it")
//                    val bundle = Bundle().apply {
//                        putString(PAYMENT_ID, it.data.paymentId)
//                    }
////                findNavController().navigateUp()
//                    findNavController().popBackStack(R.id.paymentModesFragNew, true)
//                    findNavController().navigate(R.id.paidReceiptFrag, bundle)
//                    /*val paymentStatus = Bundle().apply {
//                        putParcelable(Constants.PAYMENT_STATUS,it)
//                    }*/
////                    findNavController().navigate(R.id.transactionDoneScreenFrag)
//
////                    (activity as MainActivity).commonViewModel.getPendingInterestDues((activity as MainActivity)?.appSharedPref)
//                }
//            }
//        }
//        paymentViewModel.getRespCustomersDetailsLiveData.observe(viewLifecycleOwner) {
//            it?.let {
//                respCustomerDetail = it
//            }
//        }
//       val callback: OnBackPressedCallback =
//              object : OnBackPressedCallback(true) { //* enabled by default *//*
//                override fun handleOnBackPressed() {
//                    // Handle the back button event
//                    Log.d("TAG", "handleOnBackPressed: ..........pressed")
//                    toggleWebViewVisibility(View.GONE)
////                    findNavController().navigate(R.id.paymentCloseDialog)
//                }
//            }
//        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
//
//
//        binding.enterCardNumEt.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                // Not needed for formatting
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                val currentText = s.toString().replace("\\s".toRegex(), "")
//                val formattedText = buildString {
//                    currentText.chunked(4).forEachIndexed { index, chunk ->
//                        if (index != 0) append(" ")
//                        append(chunk)
//                    }
//                }
//                if (s.toString() != formattedText) {
//                    binding.enterCardNumEt.setText(formattedText)
//                    binding.enterCardNumEt.setSelection(formattedText.length)
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                // Not needed for formatting
//            }
//        })
//        binding.enterExpireDateEt.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(editable: Editable?) {
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                /*
//                                if (s?.length == 2 && before == 0) { // Assuming MM/YYYY format
//                                    binding.enterExpireDateEt.setText(String.format("%s/", s))
//                                    binding.enterExpireDateEt.setSelection(binding.enterExpireDateEt.text?.length ?: 0)
//                                }*/
//
//                val input = s?.toString()?.replace("\\s".toRegex(), "") ?: ""
//
//                if (input.length <= 4) {
//                    val monthPart = input.take(2).toIntOrNull() ?: 0
//                    val yearPart = input.takeLast(2).toIntOrNull() ?: 0
//
//                    val isValidMonth = monthPart in 1..12
////                    val isValidYear = yearPart in 24..40
//                    val formattedText = when {
//                        input.length == 3 && input[2] != '/' -> "${input.take(2)}/${input.drop(2)}"
//                        input.length == 1 && monthPart in 2..9 -> "0$input/"
//                        input.length == 2 && monthPart > 12 -> "1"
//                        else -> input
//                    }
//                    if (input != formattedText) {
//                        binding.enterExpireDateEt.setText(formattedText)
//                        binding.enterExpireDateEt.setSelection(formattedText.length)
//                    }
//                    if (!(isValidMonth)) {
//                        binding.enterExpireDateEt.error = "Invalid expiry date"
//                    }
//                }
//
//            }
//        })
//        Log.d(TAG, "onStart: ${binding.enterExpireDateEt.text}")
//        paymentViewModel.responseCreateOrder.observe(viewLifecycleOwner) {
//            it?.let {
//                if (it.statusCode == "200") {
//                    (activity as MainActivity).amount = it.data.amount
//                    when {
//                        bhmValue -> {
//                            Log.e("TAG", "onStart: ---bhmValue")
//                        }
//
//                        walletValue -> {
//                            Log.e("TAG", "onStart:---walletValue ")
//
//                        }
//
//                        creditValue -> {
//                            Log.e("TAG", "onStart: --creditValue")
//
//                        }
//
//                        netBanking -> {
//                            Log.e("TAG", "onStart: -netBanking")
//
//                        }
//                    }
//                    payload.put("order_id", it.data.orderId)
//                    try {
//                        sendRequest(false)
//                    } catch (e: java.lang.Exception) {
//                        Log.d("TAG", "onResponse: ....${e.message}")
//                    }
//
//                }
//            }
//        }
//        binding.spinnerNetBanking.setOnClickListener {
//            if (this::bankDialog.isInitialized) bankDialog.show()
//        }
//        binding.spinnerWallet.setOnClickListener {
//            if (this::walletDialog.isInitialized) walletDialog.show()
//        }
//        binding.PayNetBanking.setOnClickListener {
//            amountToPay?.let { it1 -> createOrder(it1, notes = "paying from netbanking") }
//        }
//        binding.PayWallet.setOnClickListener {
//            amountToPay?.let { it1 -> createOrder(it1, notes = "paying from wallet") }
//        }
//        binding.payDebitCredit.setOnClickListener {
//            if (isValidate()) {
//                payWithDebitCard()
//                amountToPay?.let { it1 -> createOrder(it1, notes = "paying from debit_or_credit") }
//            }
//        }
//        binding.gPayTv.setOnClickListener {
//            upiIntentGooglePay()
//            amountToPay?.let { it1 -> createOrder(it1, notes = "paying from g_pay_intent") }
//        }
//        binding.phonePeTv.setOnClickListener {
//
//            // Use package name which we want to check
//            // Use package name which we want to check
//            val isAppInstalled = appInstalledOrNot("com.phonepe.app")
//
//            if (isAppInstalled) {
//                //This intent will help you to launch if the package is already installed
////                val LaunchIntent: Intent? =
////                    requireActivity().packageManager.getLaunchIntentForPackage("com.phonepe.app")
////                startActivity(LaunchIntent)
//                upiIntentPhonePe()
//                amountToPay?.let { it1 -> createOrder(it1, notes = "paying from phone_pay_intent") }
//                Log.i("SampleLog", "Application is already installed.")
//            } else {
//                // Do whatever we want to do if application not installed
//                // For example, Redirect to play store
//                "Application is not currently installed.".showSnackBar()
//            }
//
//        }
//        binding.paytmTv.setOnClickListener {
//            // Use package name which we want to check
//            val isAppInstalled = appInstalledOrNot("net.one97.paytm")
//
//            if (isAppInstalled) {
//                //This intent will help you to launch if the package is already installed
////                val LaunchIntent: Intent? =
////                    requireActivity().packageManager.getLaunchIntentForPackage("com.phonepe.app")
////                startActivity(LaunchIntent)
//                upiIntentPaytm()
//                amountToPay?.let { it1 -> createOrder(it1, notes = "paying from paytm _intent") }
//                Log.i("SampleLog", "Application is already installed.")
//            } else {
//                // Do whatever we want to do if application not installed
//                // For example, Redirect to play store
//                "Application is not currently installed.".showSnackBar()
//            }
//
//        }
//        binding.otherApps.setOnClickListener {
//            otherIntent()
//            amountToPay?.let { it1 -> createOrder(it1, notes = "paying from other_app") }
//        }
//        binding.apply {
//            bhmUpiParent.setOnClickListener {
//                if (bhmValue) {
//                    bhmUpiParent.setBackgroundResource(R.drawable.rect_opem_loans)
//                    arrowDowmBhmIv.setImageResource(R.drawable.cross_icon)
//                    upiMethodParent.show()
//                    verifyUpiBtn.setOnClickListener {
//                        if (AppUtility.validateUPI(binding.enterUpiEt.text.toString())) {
//                            upiCollect(binding.enterUpiEt.text.toString())
//                            amountToPay?.let { it1 ->
//                                createOrder(
//                                    it1,
//                                    notes = "paying from upi_collect"
//                                )
//                            }
//                        } else {
//                            "Please enter valid UPI Id".showSnackBar()
//                        }

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
                    }


//                    upiMethodParent.startAnimation(
//                        AnimationUtils.loadAnimation(
//                            requireContext(), R.anim.slide_down
//                        )
//                    )
                    //wallet
//                    walletMethodParent.hide()
//                    walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
//                    //credit
//                    creditCardParent.hide()
//                    creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)
//
//                    //netBanking
//                    netBankCardParent.hide()
//                    netBankingParentParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDownNbIv.setImageResource(R.drawable.arrow_down_black)
//
//                    bhmValue = false
//                } else {
//                    upiMethodParent.hide()
////                    upiMethodParent.startAnimation(
////                        AnimationUtils.loadAnimation(
////                            requireContext(), R.anim.slide_up
////                        )
////                    )
//                    arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
//                    bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    bhmValue = true
//                }
//            }
//            walletParent.setOnClickListener {
//                if (walletValue) {
//                    arrowDownWalletIv.setImageResource(R.drawable.cross_icon)
//                    walletMethodParent.show()
//                    walletParent.setBackgroundResource(R.drawable.rect_opem_loans)
//
//                    // upi
//                    upiMethodParent.hide()
//                    bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
//                    //creditCa0rd
////                    creditCardParent.hide()
//                    creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)
//
//                    //netBanking
//                    netBankCardParent.hide()
//                    netBankingParentParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDownNbIv.setImageResource(R.drawable.arrow_down_black)
//                    walletValue = false
//
//                } else {
//                    walletMethodParent.hide()
//                    walletMethodParent.startAnimation(
//                        AnimationUtils.loadAnimation(
//                            requireContext(), R.anim.slide_up
//                        )
//                    )
//                    arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
//                    walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    walletValue = true
//                }
//            }
//            nbTv.setOnClickListener {
//                if (creditValue) {
//                    nbTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross_icon, 0)
////                    arrowDownCreditIv.setImageResource(R.drawable.cross_icon)
//                    creditDebitParent.setBackgroundResource(R.drawable.rect_opem_loans)
//                    creditCardParent.show()
//                    //wallet
//                    walletMethodParent.hide()
//                    walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
//                    //upi
//                    upiMethodParent.hide()
//                    bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
//                    //netBanking
//                    netBankCardParent.hide()
//                    netBankingParentParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDownNbIv.setImageResource(R.drawable.arrow_down_black)
//                    creditValue = false
//                } else {
//                    creditCardParent.hide()
//                    creditCardParent.startAnimation(
//                        AnimationUtils.loadAnimation(
//                            requireContext(), R.anim.slide_up
//                        )
//                    )
////                    arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)
//                    nbTv.setCompoundDrawablesWithIntrinsicBounds(
//                        0,
//                        0,
//                        R.drawable.arrow_down_black,
//                        0
//                    )
//                    creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    creditValue = true
//                }
//
//            }
//            netBankingParentParent.setOnClickListener {
//                if (netBanking) {
//                    arrowDownNbIv.setImageResource(R.drawable.cross_icon)
//                    netBankingParentParent.setBackgroundResource(R.drawable.rect_opem_loans)
//                    netBankCardParent.show()
//                    //wallet
//                    walletMethodParent.hide()
//                    walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
//                    //upi
//                    upiMethodParent.hide()
//                    bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
//
//                    //creditCa0rd
//                    creditCardParent.hide()
//                    creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)
//
//                    netBanking = false
//                } else {
//                    netBankCardParent.hide()
//                    netBankCardParent.startAnimation(
//                        AnimationUtils.loadAnimation(
//                            requireContext(), R.anim.slide_up
//                        )
//                    )
//                    arrowDownNbIv.setImageResource(R.drawable.arrow_down_black)
//                    netBankingParentParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//
//                    netBanking = true
//                }
//            }
//        }

    }

/*
    private fun isValidate(): Boolean {
        return when {
            binding.enterNameOnCardEt.text?.isEmpty() == true && binding.enterCardNumEt.text?.isEmpty() == true
                    && binding.enterExpireDateEt.text?.isEmpty() == true && binding.enterCvvEt.text?.isEmpty() == true -> {
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

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop: /////")
//        binding.webview.hide()
        payload = JSONObject()
        paymentViewModel.isCalled = false
        paymentViewModel.responseCreateOrder.removeObservers(viewLifecycleOwner)
        paymentViewModel.getRespCustomersDetailsLiveData.removeObservers(viewLifecycleOwner)
    }


    override fun onDestroy() {
        super.onDestroy()
//        binding.webview.hide()
//        toggleWebViewVisibility(View.GONE)
    }

    private fun createOrder(amount: Double, notes: String) {
        Log.d("TAG", "createOrder: ......$amount")
        paymentViewModel.createOrder(
            (activity as MainActivity).appSharedPref,
            reqCreateOrder = ReqCreateOrder(
                amount = amount,
                currency = "INR",
                custId = (activity as MainActivity).appSharedPref?.getStringValue(com.paulmerchants.gold.utility.Constants.CUSTOMER_ID)
                    .toString(),
                notes = Notes(notes, "notes_2"),
                receipt = "${AppUtility.getCurrentDate()}_${BuildConfig.VERSION_NAME}",
            )
        )
    }

    private fun toggleWebViewVisibility(webviewVisibility: Int) {
        (activity as MainActivity).binding.webview.visibility = webviewVisibility
        binding.clOuter.visibility = if (webviewVisibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun sendRequest(usePaymentResultListener: Boolean) {
        Log.d("TAG", "sendRequest: .......$payload")

//        if (this::razorpay.isInitialized) {
//            if (etApiKey.text.toString().isNotEmpty()){
//                razorpay.changeApiKey(etApiKey.text.toString())
//            }
//            val dialog = AlertDialog.Builder(requireContext())
//            dialog.setPositiveButton("Ok", object : DialogInterface.OnClickListener {
//                override fun onClick(dialog: DialogInterface?, which: Int) {}
//            })
        razorpay?.validateFields(payload, object : ValidationListener {
            override fun onValidationSuccess() {
                toggleWebViewVisibility(View.VISIBLE)
                if (usePaymentResultListener) {
                    razorpay?.submit(payload, object : PaymentResultListener {
                        override fun onPaymentSuccess(p0: String?) {
                            p0?.let {
                                Log.d("TAG", "onPaymentSuccess: .......$p0")
                                toggleWebViewVisibility(View.GONE)
//                                    dialog.setTitle("Payment Successful")
//                                    dialog.setMessage(it)
//                                    dialog.show()
                                paymentViewModel.responseCreateOrder.removeObservers(
                                    viewLifecycleOwner
                                )
                            }
                        }

                        override fun onPaymentError(p0: Int, p1: String?) {
                            toggleWebViewVisibility(View.GONE)
                            Log.d("TAG", "onPaymentError: ........$p0----$p1")
//                                dialog.setTitle("Payment Failed")
//                                dialog.setMessage(p1)
//                                dialog.show()
                            paymentViewModel.responseCreateOrder.removeObservers(
                                viewLifecycleOwner
                            )

                        }
                    })
                } else {
                    razorpay?.submit(payload, object : PaymentResultWithDataListener {
                        override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
                            p1?.let {
                                Log.d(
                                    "TAG",
                                    "onPaymentSuccess: .....signature....${p1.signature}----" +
                                            "${p1.data}-----${p1.paymentId}"
                                )
                                toggleWebViewVisibility(View.GONE)
                                if (p1.paymentId != null) {
                                    if (payAlllInOneGo != null) {
                                        (activity as MainActivity).appSharedPref?.let {
                                            updatePaymentStatusToServerToAllInOneGo(
                                                it,
                                                StatusPayment("captured", p1)
                                            )
                                        }
                                    } else {
                                        (activity as MainActivity).appSharedPref?.let {
                                            updatePaymentStatusToServer(
                                                it,
                                                StatusPayment("captured", p1),
                                                false
//                                                isCustomPay ?: false
                                            )
                                        }
                                    }
                                } else {
                                    "Unable to initiate the payment\nplease try again later".showSnackBar()
                                }


//                                    dialog.setTitle("Payment Successful")
//                                    dialog.setMessage(it.data.toString())
//                                    dialog.show()
                            }
                        }

                        override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
                            toggleWebViewVisibility(View.GONE)
                            if (p2?.paymentId != null) {
                                p2.let {
                                    if (payAlllInOneGo != null) {
                                        (activity as MainActivity).appSharedPref?.let {
                                            updatePaymentStatusToServerToAllInOneGo(
                                                it,
                                                StatusPayment("not_captured", p2)
                                            )
                                        }
                                    } else {
                                        (activity as MainActivity).appSharedPref?.let {
                                            updatePaymentStatusToServer(
                                                it,
                                                StatusPayment("not_captured", p2),
                                                false
//                                                isCustomPay ?: false
                                            )
                                        }
                                    }

//                                    dialog.setTitle("Payment Failed")
//                                    dialog.setMessage(it.data.toString())
//                                    dialog.show()
                                }
//
                            } else {
                                "Payment Failed\nplease try again later".showSnackBar()
                            }
                        }

                    })
                }

            }

            override fun onValidationError(p0: MutableMap<String, String>?) {
                Log.e(TAG, "onValidationError: ..........$p0")
//                    dialog.setMessage(p0.toString())
//                    dialog.show()
            }

        })
//        }
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm: PackageManager = requireActivity().packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "API Response : ")
        if (razorpay != null) {
            razorpay?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: .////////")
        paymentViewModel.isCalled = false
        paymentViewModel.responseCreateOrder.removeObservers(viewLifecycleOwner)
        paymentViewModel.getRespCustomersDetailsLiveData.removeObservers(viewLifecycleOwner)
    }

    private fun updatePaymentStatusToServer(
        appSharedPref: AppSharedPref,
        statusData: StatusPayment,
        isCustom: Boolean,
    ) {
        Log.d(TAG, "updatePaymentStatusToServer: $amountToPay....$statusData")
        if (customerAcc != null && amountToPay != null) {
            amountToPay?.let {
                paymentViewModel.updatePaymentStatus(
                    appSharedPref = appSharedPref,
                    status = statusData.status,
                    razorpay_payment_id = statusData.paymentData?.paymentId.toString(),
                    razorpay_order_id = statusData.paymentData?.orderId.toString(),
                    razorpay_signature = statusData.paymentData?.signature.toString(),
                    custId = appSharedPref.getStringValue(com.paulmerchants.gold.utility.Constants.CUSTOMER_ID)
                        .toString(),
                    amount = amountToPay,
                    contactCount = 0, description = "desc_payment",
                    account = customerAcc.toString(),
                    isCustom = isCustom
                )
            }
        } else {
            "Amount: Some thing went wrong".showSnackBar()
        }
    }

    private fun updatePaymentStatusToServerToAllInOneGo(
        appSharedPref: AppSharedPref,
        statusData: StatusPayment,
    ) {
        Log.d(TAG, "updatePaymentStatusToServer: $amountToPay....$statusData")
        if (amountToPay != null) {
            amountToPay?.let {
                payAlllInOneGo?.payAll?.let { payAllGo ->
                    paymentViewModel.updatePaymentStatusAllInOneGo(
                        appSharedPref = appSharedPref,
                        status = statusData.status,
                        razorpay_payment_id = statusData.paymentData?.paymentId.toString(),
                        razorpay_order_id = statusData.paymentData?.orderId.toString(),
                        razorpay_signature = statusData.paymentData?.signature.toString(),
                        custId = appSharedPref.getStringValue(com.paulmerchants.gold.utility.Constants.CUSTOMER_ID)
                            .toString(),
                        amount = amountToPay,
                        contactCount = 0,
                        description = "desc_payment",
                        listOfPaullINOneGo = payAllGo
                    )
                }
            }
        } else {
            "Amount: Some thing went wrong".showSnackBar()
        }
    }

    private fun initRazorpay() {
        razorpay = Razorpay(requireActivity(), BuildConfig.RAZORPAY_KEY)
        razorpay?.getPaymentMethods(object : PaymentMethodsCallback {
            override fun onPaymentMethodsReceived(p0: String?) {
                val bankDialogBuilder = AlertDialog.Builder(requireContext())
                val walletDialogBuilder = AlertDialog.Builder(requireContext())
                val bankListLayout = LayoutInflater.from(requireContext())
                    .inflate(R.layout.banks_list, null, false)
                val bankListView = bankListLayout.findViewById<ListView>(R.id.list_view_bank)
                val walletListLayout = LayoutInflater.from(requireContext())
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
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        bankNames
                    )
                    bankListView.adapter = banksListAdapter
                    bankDialogBuilder.setView(bankListLayout)
                    bankDialogBuilder.setTitle("Select a bank")
                    bankDialogBuilder.setPositiveButton(
                        "Ok"
                    ) { dialog, which -> }
                    bankDialog = bankDialogBuilder.create()
                    bankListView.setOnItemClickListener { parent, view, position, id ->
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
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        walletNames
                    )
                    walletListView.adapter = walletListAdapter
                    walletDialogBuilder.setView(walletListLayout)
                    walletDialogBuilder.setTitle("Select a Wallet")
                    walletDialogBuilder.setPositiveButton(
                        "Ok"
                    ) { dialog, which -> }
                    walletDialog = walletDialogBuilder.create()
                    walletListView.setOnItemClickListener { parent, view, position, id ->
                        basePayload()
                        payload.put("method", "wallet")
                        payload.put("wallet", walletNames[position])
                        binding.spinnerWallet.text = walletNames[position]
                        walletDialog.dismiss()
                    }
                }
            }

            override fun onError(p0: String?) {
                Toast.makeText(requireContext(), p0 ?: "Some thing went wrong..", Toast.LENGTH_LONG)
                    .show()
            }
        })
        razorpay?.setWebView((activity as MainActivity).binding.webview)
    }

    private fun setUpBankSpinnerForRazorPay(
        bankNames: ArrayList<String>,
        bankKeys: ArrayList<String>,
    ) {

    }

    private fun upiCollect(vpa: String) {
        basePayload()
        payload.put("method", "upi")
        payload.put("vpa", vpa)
    }


    private fun payWithDebitCard() {
        basePayload()

//        var cardNumberString = debitCardNumber?.text.toString()
//        cardNumberString = cardNumberString.replace("\\s".toRegex(), "")

        payload.put("method", "card")
        payload.put(
            "card[number]",
            binding.enterCardNumEt.text.toString().replace("\\s".toRegex(), "")
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
                "contact",
                respCustomerDetail?.respGetCustomer?.MobileNo
            )
            payload.put(
                "email",
                respCustomerDetail?.emailIdNew
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun modifyHeaders() {
        binding.apply {
            binding.headerBillMore.apply {
                backIv.setOnClickListener {
                    findNavController().popBackStack(R.id.homeScreenFrag, true)
                    findNavController().navigate(R.id.homeScreenFrag)
                }
                titlePageTv.text = getString(R.string.pay_modes)
                endIconIv.setImageResource(R.drawable.quest_circle)
                endIconIv.hide()
            }
        }
    }*/
//}