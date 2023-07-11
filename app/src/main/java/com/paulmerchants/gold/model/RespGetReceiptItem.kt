package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespGetReceiptItem(
    @SerializedName("AcNo") val AcNo: String,
    @SerializedName("AmtType") val AmtType: String,
    @SerializedName("TransDate") val TransDate: String,
    @SerializedName("TrnAmount") val TrnAmount: Int,
)