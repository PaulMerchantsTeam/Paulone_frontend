package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class RespGetOtp(
    @SerializedName
        ("mobile_no") val mobile_no: String?,
    @SerializedName
        ("user_exist") val user_exist: Boolean?,
    @SerializedName
        ("session_id") val session_id: String?,
    @SerializedName("customer_response_list") val customer_response_list: ArrayList<CustomerInfo>?
)