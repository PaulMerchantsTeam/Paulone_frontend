package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.usedModels.DeviceDetailsDTO


data class ReqCustomerOtpNew(
    @SerializedName("mobile_no") val  mobile_no: String,
    @SerializedName("otp") val  otp: String,
    @SerializedName("device_details_dto") val device_details_dto: DeviceDetailsDTO,
)
