package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class DeviceDetailsDTO(
    @SerializedName("appVersion") val appVersion: String,
    @SerializedName("appVersionCode") val appVersionCode: String,
    @SerializedName("deviceModel") val deviceModel: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("longitude") val longitude: String,
)