package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class ReqResetPin(
    @SerializedName("confirmMPin") val confirmMPin: String,
    @SerializedName("currentMPin") val currentMPin: String,
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("newMPin") val newMPin: String,
    @SerializedName("deviceDetailsDTO") val deviceDetailsDTO: DeviceDetailsDTO,
)