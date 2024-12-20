package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class RespPaymentReceipt(
    @SerializedName("acc_no") val acc_no: String?,
    @SerializedName("cust_id") val cust_id: String?,
    @SerializedName("payment_details_dto") val payment_details_dto: RespPaymentDetailsDto?,
)



