package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("otp") val otp: String,
)