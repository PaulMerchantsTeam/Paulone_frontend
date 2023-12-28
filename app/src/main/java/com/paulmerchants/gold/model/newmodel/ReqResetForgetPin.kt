package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class ReqResetForgetPin(
    @SerializedName("confirmMPin") val confirmMPin: String,
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("newMPin") val newMPin: String,
    @SerializedName("deviceDetailsDTO") val deviceDetailsDTO: DeviceDetailsDTO,
)