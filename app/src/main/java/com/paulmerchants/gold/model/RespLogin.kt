package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespLogin(
    @SerializedName("") val JWToken: String?,
    @SerializedName("") val Status: Boolean?
)