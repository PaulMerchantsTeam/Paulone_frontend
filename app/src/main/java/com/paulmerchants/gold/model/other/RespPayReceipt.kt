package com.paulmerchants.gold.model.other

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.responsemodels.RespPaymentReceipt

data class RespPayReceipt(
   @SerializedName("data") val data: RespPaymentReceipt,
   @SerializedName("message") val message: String,
   @SerializedName("response_message") val response_message: String,
   @SerializedName("status") val status: String,
   @SerializedName("status_code") val status_code: Int,
)



