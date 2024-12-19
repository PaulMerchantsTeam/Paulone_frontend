package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.usedModels.EntityPayment

data class PayReceipt(
    @SerializedName("acc_no") val acc_no: String?,
    @SerializedName("cust_id") val cust_id: String?,
    @SerializedName("payment_details_dto") val payment_details_dto: EntityPayment?,
)



