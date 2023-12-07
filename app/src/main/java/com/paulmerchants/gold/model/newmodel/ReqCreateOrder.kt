package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class ReqCreateOrder(
   @SerializedName("amount") val amount: Double,
   @SerializedName("currency") val currency: String,
   @SerializedName("custId") val custId: String,
   @SerializedName("notes") val notes: Notes,
   @SerializedName("receipt") val receipt: String
)