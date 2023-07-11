package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespPaymentNotDone(
    @SerializedName("Amount") val Amount: Double,
    @SerializedName("TrnCaption") val TrnCaption: String,
)