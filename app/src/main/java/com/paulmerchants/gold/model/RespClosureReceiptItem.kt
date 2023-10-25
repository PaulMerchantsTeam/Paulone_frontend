package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName

data class RespClosureReceiptItem(
    @SerializedName("AcNo") val AcNo: String,
    @SerializedName("AmtType") val AmtType: String,
    @SerializedName("DueDate") val DueDate: String,
    @SerializedName("TransDate") val TransDate: String,
    @SerializedName("TrnAmount") val TrnAmount: Int,
    @SerializedName("VoucherNo") val VoucherNo: Int,
)

/**
 * GetLoanClosureReceipt
 *
 *  "AcNo": "103210000008946",
"DueDate": "2022-06-11T00:00:00",
"TransDate": "2022-07-25T00:00:00",
"VoucherNo": 1281,
"AmtType": "Principal",
"TrnAmount": 12000
 */