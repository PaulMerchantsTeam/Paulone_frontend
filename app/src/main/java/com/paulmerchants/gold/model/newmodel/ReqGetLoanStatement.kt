package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class ReqGetLoanStatement(
   @SerializedName("acNo") val acNo: String,
   @SerializedName("fromDate") val fromDate: String,
   @SerializedName("toDate") val toDate: String,
   @SerializedName("deviceDetailsDTO") val deviceDetailsDTO: DeviceDetailsDTO,
)