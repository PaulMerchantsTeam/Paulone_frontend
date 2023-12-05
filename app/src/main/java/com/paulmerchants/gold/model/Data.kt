package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class Data(
   @SerializedName("mobileNo") val mobileNo: String,
   @SerializedName("otp") val otp: String
)