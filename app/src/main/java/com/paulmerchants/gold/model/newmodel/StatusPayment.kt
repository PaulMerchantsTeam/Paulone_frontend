package com.paulmerchants.gold.model.newmodel

import com.razorpay.PaymentData

data class StatusPayment(
    val status: Boolean,
    val paymentData: PaymentData?
)
