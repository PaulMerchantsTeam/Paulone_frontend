package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespFetchFest(
   @SerializedName("data") val `data`: List<DataX>?,
   @SerializedName("message") val message: String,
   @SerializedName("response_message") val response_message: String,
   @SerializedName("status") val status: String,
   @SerializedName("statusCode") val statusCode: String,
)