package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespClosureReceiptItem(
    @SerializedName("AcNo") val AcNo: String,
    @SerializedName("AmtType") val AmtType: String,
    @SerializedName("DueDate") val DueDate: String,
    @SerializedName("TransDate") val TransDate: String,
    @SerializedName("TrnAmount") val TrnAmount: Int,
    @SerializedName("VoucherNo") val VoucherNo: Int,
)