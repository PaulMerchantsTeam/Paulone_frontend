package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem

data class GeOtStandingRespObj(
  @SerializedName("current_date") val current_date: String,
  @SerializedName("get_loan_outstanding_response_data") val get_loan_outstanding_response_data: List<RespGetLoanOutStandingItem>,
)