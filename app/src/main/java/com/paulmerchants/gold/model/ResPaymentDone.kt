package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class ResPaymentDone(
    @SerializedName("BatchID") val BatchID: Int,
    @SerializedName("TransID") val TransID: Int,
    @SerializedName("VoucherNo") val VoucherNo: Int,
)