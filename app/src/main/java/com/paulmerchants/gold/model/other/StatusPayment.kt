package com.paulmerchants.gold.model.other

import com.razorpay.PaymentData

data class StatusPayment(
    val status: String?,
    val paymentData: PaymentData?
)
