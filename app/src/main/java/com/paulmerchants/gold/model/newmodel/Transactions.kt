package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class Transactions(
    @SerializedName("amount") val amount: Int,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("createdBy") val createdBy: Int,
    @SerializedName("custId") val custId: String,
    @SerializedName("id") val id: Int,
    @SerializedName("orderId") val orderId: String,
    @SerializedName("paymentId") val paymentId: String?,
    @SerializedName("processFlag") val processFlag: Int,
    @SerializedName("receiptId") val receiptId: String,
    @SerializedName("requestId") val requestId: String,
    @SerializedName("status") val status: String,
    @SerializedName("updatedAt") val updatedAt: Long,
    @SerializedName("updatedBy") val updatedBy: Int,
)