package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class RespUpdatePaymentStatus(
    @SerializedName("data") val `data`: PaymentDetail,
    @SerializedName("message") val message: String,
    @SerializedName("response_message") val response_message: String,
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val status_code: Int,
)
/**
 * {"status":"302","statusCode":"FAILURE",
 * "message":"Order not found in system",
 * "response_message":"Request Processed Successfully"}
 */