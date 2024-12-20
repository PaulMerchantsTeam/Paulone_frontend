package com.paulmerchants.gold.model.requestmodels

import com.google.gson.annotations.SerializedName

data class ReqGetOtp(
    @SerializedName("mobile_no") val mobile_no: String,
    @SerializedName("device_details_dto") val device_details_dto: ReqDeviceDetailsDTO,
)
