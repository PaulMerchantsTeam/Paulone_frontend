package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class RespGetCustomer(
    @SerializedName("data") val `data`: RespDataCustomer,
    @SerializedName("message") val message: String,
    @SerializedName("response_message") val response_message: String,
    @SerializedName("status") val status: String,
    @SerializedName("statusCode") val statusCode: String,
    @SerializedName("errorMessage") val errorMessage: String,
)
//{"responseType":"E","errorMessage":"JWT Token has expired","status":"FAILURE","statusCode":"401"}

/**
 *
 * "OutStanding": 11000
 * +,
"InterestDue": 0,
 */