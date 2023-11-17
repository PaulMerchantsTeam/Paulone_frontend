package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class ReGetLoanClosureReceipNew(
    @SerializedName("AcNo") val  AcNo: String,
    @SerializedName("deviceDetailsDTO") val deviceDetailsDTO: DeviceDetailsDTO,
)
