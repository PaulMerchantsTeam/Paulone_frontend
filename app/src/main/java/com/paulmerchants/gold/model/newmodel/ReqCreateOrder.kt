package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import retrofit2.http.Query

data class ReqCreateOrder(
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("custId") val custId: String,
    @SerializedName("notes") val notes: Notes,
    @SerializedName("accNo") val accNo: String,
    @SerializedName("receipt") val receipt: String,
    @SerializedName("makerId") val makerId: String,
    @SerializedName("submit") val submit: Boolean,
    @SerializedName("macId") val macId: String,
    @SerializedName("valueDate") val valueDate: String,
)