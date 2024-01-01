package com.paulmerchants.gold.ui.others

import android.R.attr.editable
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
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
    private var payAlllInOneGo: PayAllnOneGoDataTobeSent? = null
    private var amountToPay: Double? = 0.0
    private var customerAcc: String? = null
    private var isCustomPay: Boolean? = false
    private var payload = JSONObject()
    private lateinit var razorpay: Razorpay
    private lateinit var banksListAdapter: ArrayAdapter<String>
    private lateinit var walletListAdapter: ArrayAdapter<String>
    private lateinit var bankDialog: AlertDialog
    private lateinit var walletDialog: AlertDialog
    private val paymentViewModel: PaymentViewModel by viewModels()

    override fun PaymentsModeNewBinding.initialize() {
        amountToPay = arguments?.getDouble(Constants.AMOUNT_PAYABLE)
        customerAcc = arguments?.getString(Constants.CUST_ACC)
        isCustomPay = arguments?.getBoolean(Constants.IS_CUSTOM_AMOUNT)
        payAlllInOneGo =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(
                Constants.PAY_ALL_IN_GO_DATA, PayAllnOneGoDataTobeSent::class.java
            ) else arguments?.getParcelable<PayAllnOneGoDataTobeSent>(Constants.PAY_ALL_IN_GO_DATA) as PayAllnOneGoDataTobeSent?
        Log.e("TAGGGGGGG", "initialize: -customerAcc-----------$payAlllInOneGo------$amountToPay")
        modifyHeaders()
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
        if (amountToPay != 0.0) {
            binding.amountPaidTv.text = "${getString(R.string.Rs)}$amountToPay"
        } else {
            binding.amountPaidTv.text = "${getString(R.string.Rs)}${payAlllInOneGo?.amount ?: 0}"
            amountToPay = payAlllInOneGo?.amount
        }
        var proceedBtn = "1"
        var bhmValue = true
        var walletValue = true
        var creditValue = true
        var netBanking = true
        initRazorpay()

        paymentViewModel.respPaymentUpdate.observe(viewLifecycleOwner) {
            it?.let {
                Log.d(TAG, "ojnnnnnn: /.................${it.status}")

//                if (it.status == "200") {
                Log.d(TAG, "ojnnnnnn: /.................$it")
                val bundle = Bundle().apply {
                    putString(PAYMENT_ID, it.data.paymentId)
                }
                findNavController().navigateUp()
//                    findNavController().popBackStack(R.id.paymentModesFragNew, true)
//                    findNavController().navigate(R.id.paidReceiptFrag, bundle)
                /*val paymentStatus = Bundle().apply {
                    putParcelable(Constants.PAYMENT_STATUS,it)
                }*/
//                    findNavController().navigate(R.id.transactionDoneScreenFrag)

//                    (activity as MainActivity).commonViewModel.getPendingInterestDues((activity as MainActivity)?.appSharedPref)
//                }
            }
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) { /* enabled by default */
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    Log.d("TAG", "handleOnBackPressed: ..........pressed")
                    findNavController().navigate(R.id.paymentCloseDialog)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)


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

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /*
                                if (s?.length == 2 && before == 0) { // Assuming MM/YYYY format
                                    binding.enterExpireDateEt.setText(String.format("%s/", s))
                                    binding.enterExpireDateEt.setSelection(binding.enterExpireDateEt.text?.length ?: 0)
                                }*/

                val input = s?.toString()?.replace("\\s".toRegex(), "") ?: ""

                if (input.length <= 4) {
                    val monthPart = input.take(2).toIntOrNull() ?: 0
                    val yearPart = input.takeLast(2).toIntOrNull() ?: 0

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
        Log.d(TAG, "onStart: ${binding.enterExpireDateEt.text}")
        paymentViewModel.responseCreateOrder.observe(viewLifecycleOwner) {
            it?.let {
                if (it.statusCode == "200") {
                    (activity as MainActivity).amount = it.data.amount
                    when {
                        bhmValue -> {
                            Log.e("TAG", "onStart: ---bhmValue")
                        }

                        walletValue -> {
                            Log.e("TAG", "onStart:---walletValue ")

                        }

                        creditValue -> {
                            Log.e("TAG", "onStart: --creditValue")

                        }

                        netBanking -> {
                            Log.e("TAG", "onStart: -netBanking")

                        }
                    }
                    payload.put("order_id", it.data.orderId)
                    try {
                        sendRequest(false)
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
            amountToPay?.let { it1 -> createOrder(it1) }
        }
        binding.PayWallet.setOnClickListener {
            amountToPay?.let { it1 -> createOrder(it1) }
        }
        binding.payDebitCredit.setOnClickListener {

            if (isValidate()) {
                payWithDebitCard()
                amountToPay?.let { it1 -> createOrder(it1) }
            }
        }
        binding.gPayTv.setOnClickListener {
            upiIntentGooglePay()
            amountToPay?.let { it1 -> createOrder(it1) }
        }
        binding.phonePeTv.setOnClickListener {
            upiIntentPhonePe()
            amountToPay?.let { it1 -> createOrder(it1) }
        }
        binding.paytmTv.setOnClickListener {
            upiIntentPaytm()
            amountToPay?.let { it1 -> createOrder(it1) }
        }
        binding.otherApps.setOnClickListener {
            otherIntent()
            amountToPay?.let { it1 -> createOrder(it1) }
        }
        binding.apply {
            bhmUpiParent.setOnClickListener {
                if (bhmValue) {

                    bhmUpiParent.setBackgroundResource(R.drawable.rect_opem_loans)
                    arrowDowmBhmIv.setImageResource(R.drawable.cross_icon)
                    upiMethodParent.show()
                    verifyUpiBtn.setOnClickListener {
                        if (AppUtility.validateUPI(binding.enterUpiEt.text.toString())) {
                            upiCollect(binding.enterUpiEt.text.toString())
                            amountToPay?.let { it1 -> createOrder(it1) }
                        } else {
                            "Please enter valid UPI Id".showSnackBar()
                        }

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
            }
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
                            requireContext(), R.anim.slide_up
                        )
                    )
                    arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
                    walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    walletValue = true
                }
            }
            creditDebitParent.setOnClickListener {
                if (creditValue) {
                    arrowDownCreditIv.setImageResource(R.drawable.cross_icon)
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
                            requireContext(), R.anim.slide_up
                        )
                    )
                    arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)
                    creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    creditValue = true
                }

            }
            netBankingParentParent.setOnClickListener {
                if (netBanking) {
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
                            requireContext(), R.anim.slide_up
                        )
                    )
                    arrowDownNbIv.setImageResource(R.drawable.arrow_down_black)
                    netBankingParentParent.setBackgroundResource(R.drawable.card_sky_rect_6)

                    netBanking = true
                }
            }
        }

    }


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

    private fun createOrder(amount: Double) {
        Log.d("TAG", "createOrder: ......$amount")
        paymentViewModel.createOrder(
            (activity as MainActivity).appSharedPref,
            reqCreateOrder = ReqCreateOrder(
                amount = amount,
                currency = "INR",
                custId = (activity as MainActivity).appSharedPref?.getStringValue(com.paulmerchants.gold.utility.Constants.CUSTOMER_ID)
                    .toString(),
                notes = Notes("n_1_test", "n_2_test"),
                receipt = "rec__",
            )
        )
    }

    private fun toggleWebViewVisibility(webviewVisibility: Int) {
        binding.webview.visibility = webviewVisibility
        binding.clOuter.visibility = if (webviewVisibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun sendRequest(usePaymentResultListener: Boolean) {
        Log.d("TAG", "sendRequest: .......$payload")

        if (this::razorpay.isInitialized) {
//            if (etApiKey.text.toString().isNotEmpty()){
//                razorpay.changeApiKey(etApiKey.text.toString())
//            }
//            val dialog = AlertDialog.Builder(requireContext())
//            dialog.setPositiveButton("Ok", object : DialogInterface.OnClickListener {
//                override fun onClick(dialog: DialogInterface?, which: Int) {}
//            })
            razorpay.validateFields(payload, object : ValidationListener {
                override fun onValidationSuccess() {
                    toggleWebViewVisibility(View.VISIBLE)
                    if (usePaymentResultListener) {
                        razorpay.submit(payload, object : PaymentResultListener {
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
                        razorpay.submit(payload, object : PaymentResultWithDataListener {
                            override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
                                p1?.let {
                                    Log.d(
                                        "TAG",
                                        "onPaymentSuccess: .....signature....${p1.signature}----" +
                                                "${p1.data}-----${p1.paymentId}"
                                    )
                                    toggleWebViewVisibility(View.GONE)
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

//                                    dialog.setTitle("Payment Successful")
//                                    dialog.setMessage(it.data.toString())
//                                    dialog.show()
                                }
                            }

                            override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
                                p2?.let {
                                    toggleWebViewVisibility(View.GONE)
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
        }
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
                    findNavController(),
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
                        findNavController(),
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
        razorpay.getPaymentMethods(object : PaymentMethodsCallback {
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
        razorpay.setWebView(binding.webview)
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
                (activity as MainActivity).appSharedPref?.getStringValue(com.paulmerchants.gold.utility.Constants.CUST_MOBILE)
            )
            payload.put("email", "a@a.com")

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
    }


}