package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("festName") val festName: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("showAtCurrentDate") val showAtCurrentDate: Boolean?,
    @SerializedName("statusColor") val statusColor: String?,
)