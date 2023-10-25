package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespLoanStatmentItem(
    @SerializedName("Balance") val Balance: String,
    @SerializedName("Fine") val Fine: Int,
    @SerializedName("Interest") val Interest: Int,
    @SerializedName("OtherCharges") val OtherCharges: Int,
    @SerializedName("Particulars") val Particulars: String,
    @SerializedName("Principal") val Principal: Int,
    @SerializedName("TransDate") val TransDate: String,
    @SerializedName("TransID") val TransID: Int,
    @SerializedName("TrnMode") val TrnMode: String,
    @SerializedName("TrnType") val TrnType: String,
)

/**
 * [
{
"TransDate": "2023-06-27T00:00:00",
"TransID": 6365049,
"Principal": 500,
"Interest": 20,
"Fine": 0,
"OtherCharges": 480,
"TrnType": "Credit",
"Particulars": "MobileApp Loan Collection",
"Balance": "500.00 Cr",
"TrnMode": "Cash"
}
]
 */