package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class AcquirerData(
   @SerializedName("auth_code") val auth_code: String,
   @SerializedName("id") val id: Int,
   @SerializedName("rrn") val rrn: String
)