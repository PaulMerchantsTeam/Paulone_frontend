package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespLogin(
    @SerializedName("JWToken") val JWToken: String?,
    @SerializedName("Status") val Status: Boolean?
)