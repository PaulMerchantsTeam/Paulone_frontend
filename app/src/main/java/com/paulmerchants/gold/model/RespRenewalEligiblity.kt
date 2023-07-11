package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespRenewalEligiblity(
   @SerializedName("DueDate") val DueDate: Any,
   @SerializedName("EligibleAmt") val EligibleAmt: Any,
   @SerializedName("ErrorMsg") val ErrorMsg: Any,
   @SerializedName("IsEligible") val IsEligible: Any,
   @SerializedName("RenewalDate") val RenewalDate: Any,
)