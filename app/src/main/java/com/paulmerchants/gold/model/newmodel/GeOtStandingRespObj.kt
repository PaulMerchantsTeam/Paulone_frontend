package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem

data class GeOtStandingRespObj(
  @SerializedName("currentDate") val currentDate: String,
  @SerializedName("getLoanOutstandingResponseData") val getLoanOutstandingResponseData: List<RespGetLoanOutStandingItem>,
)