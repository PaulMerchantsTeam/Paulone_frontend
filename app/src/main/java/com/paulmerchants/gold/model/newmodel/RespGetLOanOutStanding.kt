package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.usedModels.GeOtStandingRespObj

data class RespGetLOanOutStanding(
    @SerializedName("data") val `data`: GeOtStandingRespObj,
    @SerializedName("message") val message: String?,
    @SerializedName("response_message") val response_message: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("status_code") val status_code: Int?,
    @SerializedName("error_message") val error_message: String?,
)
//{"responseType":"E","errorMessage":"JWT Token has expired","status":"FAILURE","statusCode":"401"}

/**
 *
 * "OutStanding": 11000
 * +,
"InterestDue": 0,
 */

