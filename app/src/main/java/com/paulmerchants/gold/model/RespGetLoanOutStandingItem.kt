package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespGetLoanOutStandingItem(
    @SerializedName("AcNo") val AcNo: Long,
    @SerializedName("ClosedDate") val ClosedDate: Any,
    @SerializedName("DueDate") val DueDate: String,
    @SerializedName("Fine") val Fine: Int,
    @SerializedName("InterestPeriod") val InterestPeriod: Int,
    @SerializedName("IsClosed") val IsClosed: Boolean,
    @SerializedName("OpenDate") val OpenDate: String,
    @SerializedName("OpeningAmount") val OpeningAmount: Int,
    @SerializedName("OutStanding") val OutStanding: Int,
    @SerializedName("ProductName") val ProductName: String,
)