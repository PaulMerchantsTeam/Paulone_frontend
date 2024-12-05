package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class RespUnderMain(
    @SerializedName("data")
    val data: DataDown,
    @SerializedName("message")
    val message: String,
    @SerializedName("response_message")
    val response_message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_code")
    val status_code: Int,

)

data class DataDown(
    @SerializedName("down") val down: Boolean,
    @SerializedName("id") val id: Int,
    @SerializedName("start_time") val start_time: String?,
    @SerializedName("end_time") val end_time: String?,
    @SerializedName("current_time") val current_time: String?,
)
