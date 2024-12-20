package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class RespCustomersDetails(
    @SerializedName("aadhaar_no") val aadhaar_no: String?,
    @SerializedName("display_name") val display_name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("mailing_address") val mailing_address: String?,
    @SerializedName("mobile_no") val mobile_no: String?,
    @SerializedName("pan") val pan: String?,
    @SerializedName("photo") val photo: String?,
    @SerializedName("created_at") val created_at: Long?,
    @SerializedName("updated_at") val updated_at: Long?,
    @SerializedName("id") val id: Long?,
)

