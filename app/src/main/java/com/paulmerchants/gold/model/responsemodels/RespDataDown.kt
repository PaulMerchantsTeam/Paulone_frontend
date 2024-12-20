package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class RespDataDown(
    @SerializedName("down") val down: Boolean,
    @SerializedName("id") val id: Int,
    @SerializedName("start_time") val start_time: String?,
    @SerializedName("end_time") val end_time: String?,
    @SerializedName("current_time") val current_time: String?,
)


