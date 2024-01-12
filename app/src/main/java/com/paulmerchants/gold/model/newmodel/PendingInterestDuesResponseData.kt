package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class PendingInterestDuesResponseData(
    @SerializedName("acNo") val acNo: Long,
    @SerializedName("dueDate") val dueDate: String,
    @SerializedName("fine") val fine: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("interestDue") val interestDue: Int,
    @SerializedName("payableAmount") val payableAmount: Int,
    @SerializedName("productName") val productName: String,
    @SerializedName("rebateAmount") val rebateAmount: Int,
    val currentDate: String,
)