package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespCustomersDetails(
    @SerializedName("AadhaarNo") val AadhaarNo: String,
    @SerializedName("DisplayName") val DisplayName: String,
    @SerializedName("Email") val Email: String,
    @SerializedName("MailingAddress") val MailingAddress: String,
    @SerializedName("MobileNo") val MobileNo: String,
    @SerializedName("PAN") val PAN: String,
)