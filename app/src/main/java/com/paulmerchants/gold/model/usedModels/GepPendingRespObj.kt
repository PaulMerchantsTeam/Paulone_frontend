package com.paulmerchants.gold.model.usedModels

import com.google.gson.annotations.SerializedName

data class GepPendingRespObj(
  @SerializedName("current_date") val current_date: String?,
  @SerializedName("pending_interest_dues_response_data") val pending_interest_dues_response_data: List<GetPendingInrstDueRespItem>?
)
