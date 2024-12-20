package com.paulmerchants.gold.model.other

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.responsemodels.RespLoginData

data class RespLoginWithMpin(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("status_code") val status_code: Int?,
    @SerializedName("response_message") val response_message: String?,

    @SerializedName("data") val data: RespLoginData,
)
{


}