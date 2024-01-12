package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class PaymentMethod(
    @SerializedName("CreditCard") val CreditCard: Boolean,
    @SerializedName("DebitCard") val DebitCard: Boolean,
    @SerializedName("Netbanking") val Netbanking: Boolean,
    @SerializedName("UPI") val UPI: Boolean,
    @SerializedName("Wallet") val Wallet: Boolean,
)