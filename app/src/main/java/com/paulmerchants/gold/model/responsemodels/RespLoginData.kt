package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class RespLoginData(
   @SerializedName("refresh_token") val refresh_token: String?,
   @SerializedName("token") val token: String?
)