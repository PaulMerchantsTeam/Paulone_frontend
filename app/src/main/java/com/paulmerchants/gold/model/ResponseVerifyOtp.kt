package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class ResponseVerifyOtp(
    @SerializedName("data") val data: Any,  //for dynamic change in data type....
    @SerializedName("message") val message: String,
    @SerializedName("response_message") val response_message: String,
    @SerializedName("status") val status: String,
    @SerializedName("statusCode") val statusCode: String,
    @SerializedName("userExist") val userExist: Boolean,
)

