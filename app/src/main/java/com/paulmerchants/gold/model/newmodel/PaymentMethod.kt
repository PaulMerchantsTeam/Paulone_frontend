package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class PaymentMethod(

    @SerializedName("method") val method: String?,
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("updated_at") val updated_at: String?,
    @SerializedName("value") val value: Boolean,


//    @SerializedName("CreditCard") val CreditCard: Boolean,
//    @SerializedName("DebitCard") val DebitCard: Boolean,
//    @SerializedName("Netbanking") val Netbanking: Boolean,
//    @SerializedName("UPI_INTENT") val UPI_INTENT: Boolean,
//    @SerializedName("UPI_COLLECT") val UPI_COLLECT: Boolean,
//    @SerializedName("Wallet") val Wallet: Boolean,
)