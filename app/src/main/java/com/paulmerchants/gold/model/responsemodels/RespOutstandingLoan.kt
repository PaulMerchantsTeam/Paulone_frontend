package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class RespOutstandingLoan(
  @SerializedName("current_date") val current_date: String,
  @SerializedName("get_loan_outstanding_response_data") val get_loan_outstanding_response_data: List<RespGetLoanOutStandingItem>,
)