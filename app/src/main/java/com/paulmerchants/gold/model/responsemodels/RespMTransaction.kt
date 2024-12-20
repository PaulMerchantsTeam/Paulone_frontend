package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class RespMTransaction(
    @SerializedName("payment_id") val payment_id: String,
    @SerializedName("order_id") val order_id: String,
)
