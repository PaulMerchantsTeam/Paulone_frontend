package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.usedModels.DeviceDetailsDTO

data class ReqResetForgetPin(
    @SerializedName("confirm_mpin") val confirm_mpin: String,
    @SerializedName("mobile_no") val mobile_no: String,
    @SerializedName("new_mpin") val new_mpin: String,
    @SerializedName("device_details_dto") val device_details_dto: DeviceDetailsDTO,
)