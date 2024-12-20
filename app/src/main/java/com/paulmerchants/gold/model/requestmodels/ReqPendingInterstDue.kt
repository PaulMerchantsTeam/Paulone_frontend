package com.paulmerchants.gold.model.requestmodels

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.requestmodels.ReqDeviceDetailsDTO

data class ReqPendingInterstDue(
    @SerializedName("cust_id") val  cust_id: String,
    @SerializedName("device_details_dto") val device_details_dto: ReqDeviceDetailsDTO,
)
