package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class ReqCustomerNew(
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("deviceDetailsDTO") val deviceDetailsDTO: DeviceDetailsDTO,
)
