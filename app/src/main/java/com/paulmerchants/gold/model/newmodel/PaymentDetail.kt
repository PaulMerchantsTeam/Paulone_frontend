package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class PaymentDetail(
    @SerializedName("paymentId") val paymentId: String,
)
