package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class ReqCustomerNew(
    @SerializedName("mobile_no") val mobile_no: String,
    @SerializedName("device_details_dto") val device_details_dto: DeviceDetailsDTO,
)
