package com.paulmerchants.gold.model.requestmodels

import com.google.gson.annotations.SerializedName

data class ReqCreateOrder(
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String,
    @SerializedName("cust_id") val cust_id: String,
    @SerializedName("notes") val notes: Notes,
    @SerializedName("acc_no") val acc_no: String,
    @SerializedName("receipt") val receipt: String,
    @SerializedName("maker_id") val maker_id: String,
    @SerializedName("submit") val submit: Boolean,
    @SerializedName("mac_id") val mac_id: String,
    @SerializedName("value_date") val value_date: String,
)
