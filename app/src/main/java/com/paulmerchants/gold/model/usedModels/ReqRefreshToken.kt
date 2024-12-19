package com.paulmerchants.gold.model.usedModels

import com.google.gson.annotations.SerializedName

data class ReqRefreshToken(
    @SerializedName("token") val token: String?
)
