package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespGetCustomerItem(
    @SerializedName("Cust_ID") val Cust_ID: Long?,
    @SerializedName("CustName") val CustName: String?,
    @SerializedName("Status") val Status: Boolean?,
)


/**
 *
 */