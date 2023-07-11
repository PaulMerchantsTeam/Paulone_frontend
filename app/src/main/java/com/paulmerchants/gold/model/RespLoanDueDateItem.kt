package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespLoanDueDateItem(
    @SerializedName("AcNo") val AcNo: String,
    @SerializedName("DueDate") val DueDate: String,
    @SerializedName("ProductName") val ProductName: String,
)