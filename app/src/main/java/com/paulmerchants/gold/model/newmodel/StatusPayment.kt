package com.paulmerchants.gold.model.newmodel

import com.razorpay.PaymentData

data class StatusPayment(
    val status: String?,
    val paymentData: PaymentData?
)
