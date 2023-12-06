package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class TokenExpiredResp(
    @SerializedName("errorMessage") val errorMessage: String,
    @SerializedName("responseType") val responseType: String,
    @SerializedName("status") val status: String,
    @SerializedName("statusCode") val statusCode: String,
)