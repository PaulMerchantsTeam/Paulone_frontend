package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class RespAllBranch(
    @SerializedName("data") val `data`: List<PmlBranch>,
    @SerializedName("message")  val message: String,
    @SerializedName("response_message")  val response_message: String,
    @SerializedName("status") val status: String,
    @SerializedName("statusCode") val statusCode: String
)