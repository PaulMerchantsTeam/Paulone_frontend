package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RequestLogin(
    @SerializedName("Password") val Password: String,
    @SerializedName("Username") val Username: String
)