package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class RespPendingInterestDue(
  @SerializedName("current_date") val current_date: String?,
  @SerializedName("pending_interest_dues_response_data") val pending_interest_dues_response_data: List<PendingInterestDuesResponseData>?
)
