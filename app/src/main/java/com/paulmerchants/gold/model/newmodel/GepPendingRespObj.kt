package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem

data class GepPendingRespObj(
  @SerializedName("currentDate") val currentDate: String,
  @SerializedName("pendingInterestDuesResponseData") val pendingInterestDuesResponseData: List<GetPendingInrstDueRespItem>
)