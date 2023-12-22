package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class RespLoginWithMpin(
    @SerializedName("data") val `data`: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("response_message") val response_message: String,
    @SerializedName("status") val status: String,
    @SerializedName("statusCode") val statusCode: String,
)