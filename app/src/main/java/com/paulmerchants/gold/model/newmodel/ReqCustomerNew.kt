package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.usedModels.DeviceDetailsDTO

data class ReqCustomerNew(
    @SerializedName("mobile_no") val mobile_no: String,
    @SerializedName("device_details_dto") val device_details_dto: DeviceDetailsDTO,
)
