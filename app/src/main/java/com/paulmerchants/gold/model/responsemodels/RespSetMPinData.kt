package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.requestmodels.ReqDeviceDetailsDTO

data class RespSetMPinData(
    @SerializedName("created_at") val created_at: Long,
    @SerializedName("id") val id: Int,
    @SerializedName("full_name") val full_name: String?,
    @SerializedName("email_id") val email_id: String?,
    @SerializedName("mobile_no") val mobile_no: String?,
    @SerializedName("confirm_mpin") val confirm_mpin: String?,
    @SerializedName("terms_and_condition") val terms_and_condition: Boolean?,
    @SerializedName("device_details_dto") val device_details_dto: ReqDeviceDetailsDTO?,
)