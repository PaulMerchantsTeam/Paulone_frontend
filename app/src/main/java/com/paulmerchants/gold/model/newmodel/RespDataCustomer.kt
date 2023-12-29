package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class RespDataCustomer(
    @SerializedName("apiResponse") val apiResponse: String,
    @SerializedName("email") val email: String,
)