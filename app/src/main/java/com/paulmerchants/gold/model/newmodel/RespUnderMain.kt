package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class RespUnderMain(
    @SerializedName("data")
    val `data`: DataDown,
    @SerializedName("message")
    val message: String,
    @SerializedName("response_message")
    val response_message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("statusCode")
    val statusCode: String,
    @SerializedName("errorMessage")
    val errorMessage: String,
)

data class DataDown(
    @SerializedName("down") val down: Boolean,
    @SerializedName("id") val id: Int,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("endTime") val endTime: String?,
)
