package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespLoanRenewalProcess(
   @SerializedName("AcNo") val AcNo: Any,
   @SerializedName("ErrorMsg") val ErrorMsg: Any,
   @SerializedName("IsSuccess") val IsSuccess: Any,
   @SerializedName("TransID") val TransID: Any,
)