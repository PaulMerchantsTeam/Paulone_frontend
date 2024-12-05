package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class ResponseGetOtp(
    @SerializedName("data") val data: Any,
    @SerializedName("message") val message: String,
    @SerializedName("response_message") val response_message: String,
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val status_code: Int,
    @SerializedName("user_exist") val user_exist: Boolean,
)

