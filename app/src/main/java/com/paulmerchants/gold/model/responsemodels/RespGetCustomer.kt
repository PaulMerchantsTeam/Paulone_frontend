package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class RespGetCustomer(
    @SerializedName("api_response") val api_response: RespCustomersDetails,
    @SerializedName("email") val email: String,
)


