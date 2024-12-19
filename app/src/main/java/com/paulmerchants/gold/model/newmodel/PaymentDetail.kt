package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class PaymentDetail(
    @SerializedName("payment_id") val payment_id: String,
    @SerializedName("order_id") val order_id: String,
)
