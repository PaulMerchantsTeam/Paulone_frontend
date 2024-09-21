package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class Transactions(
    @SerializedName("amount") val amount: Double,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("createdBy") val createdBy: Int,
    @SerializedName("custId") val custId: String,
    @SerializedName("id") val id: Int,
    @SerializedName("orderId") val orderId: String?,
    @SerializedName("paymentId") val paymentId: String?,
    @SerializedName("processFlag") val processFlag: Int,
    @SerializedName("receiptId") val receiptId: String,
    @SerializedName("requestId") val requestId: String,
    @SerializedName("status") val status: String,
    @SerializedName("updatedAt") val updatedAt: Long,
    @SerializedName("accNo") val accNo: String,
    @SerializedName("submit") val submit: Boolean,
    @SerializedName("valueDate") val valueDate: String,
    @SerializedName("makerId") val makerId: String,
    @SerializedName("macId") val macId: String,


    )





