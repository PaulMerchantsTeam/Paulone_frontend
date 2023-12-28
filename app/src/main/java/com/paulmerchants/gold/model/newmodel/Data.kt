package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount") val amount: Double,
    @SerializedName("amount_due") val amount_due: Double,
    @SerializedName("amount_paid") val amount_paid: Double,
    @SerializedName("attempts") val attempts: Int,
    @SerializedName("created_at") val created_at: Int,
    @SerializedName("currency") val currency: String,
    @SerializedName("custId") val custId: String,
    @SerializedName("entity") val entity: String,
    @SerializedName("notes") val notes: List<String>,
    @SerializedName("offerId") val offerId: String,
    @SerializedName("orderId") val orderId: String,
    @SerializedName("receiptId") val receiptId: String,
    @SerializedName("requestId") val requestId: String,
    @SerializedName("response_message") val response_message: String,
    @SerializedName("status") val status: String,
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("otp") val otp: String,
)