package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.RespCustomersDetails

data class RespDataCustomer(
    @SerializedName("api_response") val api_response: RespCustomersDetails,
    @SerializedName("email") val email: String,
)


