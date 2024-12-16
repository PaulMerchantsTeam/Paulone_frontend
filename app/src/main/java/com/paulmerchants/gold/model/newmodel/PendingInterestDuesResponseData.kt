package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class PendingInterestDuesResponseData(
    @SerializedName("ac_no") val ac_no: Long?,
    @SerializedName("due_date") val due_date: String?,
    @SerializedName("fine") val fine: Int?,
    @SerializedName("id") val id: Int?,
    @SerializedName("interest_due") val interest_due: Int?,
    @SerializedName("payable_amount") val payable_amount: Int?,
    @SerializedName("product_name") val product_name: String?,
    @SerializedName("rebate_amount") val rebate_amount: Int?,
    var current_date: String,
)
