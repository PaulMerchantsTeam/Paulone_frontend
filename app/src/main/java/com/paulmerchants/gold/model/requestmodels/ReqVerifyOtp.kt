package com.paulmerchants.gold.model.requestmodels

import com.google.gson.annotations.SerializedName


data class ReqCustomerOtpNew(
    @SerializedName("mobile_no") val  mobile_no: String,
    @SerializedName("otp") val  otp: String,
    @SerializedName("device_details_dto") val device_details_dto: ReqDeviceDetailsDTO,
)
