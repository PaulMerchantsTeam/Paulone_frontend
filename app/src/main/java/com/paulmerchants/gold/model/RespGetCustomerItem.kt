package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespGetCustomerItem(
    @SerializedName("CustID") val CustID: Long?,
    @SerializedName("CustName") val CustName: String?,
    @SerializedName("Status") val Status: Boolean?
)


/**
 *
 */