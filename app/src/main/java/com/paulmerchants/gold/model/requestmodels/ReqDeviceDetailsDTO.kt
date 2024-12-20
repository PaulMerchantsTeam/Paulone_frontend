package com.paulmerchants.gold.model.requestmodels

import com.google.gson.annotations.SerializedName

data class ReqDeviceDetailsDTO(
    @SerializedName("app_version") val app_version: String,
    @SerializedName("app_version_code") val app_version_code: String,
    @SerializedName("device_model") val device_model: String,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("device_os") val device_os: String,
    @SerializedName("device_name") val device_name: String,
)