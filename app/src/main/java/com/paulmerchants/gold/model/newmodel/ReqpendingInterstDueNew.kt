package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.usedModels.DeviceDetailsDTO

data class ReqpendingInterstDueNew(
    @SerializedName("cust_id") val  cust_id: String,
    @SerializedName("device_details_dto") val device_details_dto: DeviceDetailsDTO,
)
