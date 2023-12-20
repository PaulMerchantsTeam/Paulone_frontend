package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class ReqPayAlInOnGo(
   @SerializedName("deviceDetailsDTO") val deviceDetailsDTO: DeviceDetailsDTO,
   @SerializedName("payAll") val payAll: List<PayAll>
)