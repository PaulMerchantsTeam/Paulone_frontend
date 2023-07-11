package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class ErrorResp(
    @SerializedName("Message") val Message: String,
)