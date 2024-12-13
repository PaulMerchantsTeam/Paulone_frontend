package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespSetMpin(
   @SerializedName("data") val data: SetMPinData,
   @SerializedName("message") val message: String,
   @SerializedName("response_message") val response_message: String,
   @SerializedName("response_type") val response_type: String,
   @SerializedName("status") val status: String,
   @SerializedName("status_code") val status_code: Int,
   @SerializedName("error_message") val error_message: String
)