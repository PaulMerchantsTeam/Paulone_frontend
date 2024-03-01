package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class PayReceipt(
    @SerializedName("accNo") val accNo: String?,
    @SerializedName("entityPayment") val entityPayment: EntityPayment?,
)