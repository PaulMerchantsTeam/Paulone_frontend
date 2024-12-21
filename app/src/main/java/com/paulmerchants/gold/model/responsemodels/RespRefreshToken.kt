package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class RespRefreshToken(
    @SerializedName("token") val token: String?
)
