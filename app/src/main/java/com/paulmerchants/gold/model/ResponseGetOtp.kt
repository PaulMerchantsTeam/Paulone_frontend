package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.newmodel.RespGetOtp

data class ResponseGetOtp(
    @SerializedName("data") val data: RespGetOtp,
    @SerializedName("message") val message: String?,
    @SerializedName("response_message") val response_message: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("status_code") val status_code: Int?,


    )

