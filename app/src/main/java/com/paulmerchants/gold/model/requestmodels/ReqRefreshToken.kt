package com.paulmerchants.gold.model.requestmodels

import com.google.gson.annotations.SerializedName

data class ReqRefreshToken(
    @SerializedName("token") val token: String?
)
