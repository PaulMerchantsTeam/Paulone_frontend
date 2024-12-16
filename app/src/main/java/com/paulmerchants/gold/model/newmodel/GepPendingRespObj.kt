package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem

data class GepPendingRespObj(
  @SerializedName("current_date") val current_date: String?,
  @SerializedName("pending_interest_dues_response_data") val pending_interest_dues_response_data: List<GetPendingInrstDueRespItem>?
)
