package com.paulmerchants.gold.model.usedModels

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount") val amount: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("amount_due") val amount_due: Int?,
    @SerializedName("amount_paid") val amount_paid: Int?,
    @SerializedName("attempts") val attempts: Int?,
    @SerializedName("created_at") val created_at: Int,
    @SerializedName("currency") val currency: String?,
    @SerializedName("cust_id") val cust_id: String?,
    @SerializedName("entity") val entity: String?,
    @SerializedName("notes") val notes: List<String>,
    @SerializedName("offer_id") val offer_id: String?,
    @SerializedName("order_id") val order_id: String?,
    @SerializedName("receipt_id") val receipt_id: String?,
    @SerializedName("request_id") val request_id: String?,
    @SerializedName("response_message") val response_message: String?,
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("status_code") val status_code: Int?,
    @SerializedName("otp") val otp: String,
    @SerializedName("acc_no") val acc_no: String?,
    @SerializedName("submit") val submit: Boolean?,
    @SerializedName("value_date") val value_date: String?,
    @SerializedName("mac_id") val mac_id: String,
    @SerializedName("maker_id") val maker_id: String?
)


/**
 * "accNo": "102210000015920",
 *     "submit": true,
 *     "valueDate": "2024-03-18T17:37:54.204593",
 *     "makerId": "12545as",
 *     "macId": "SP1A.210812.016"
 */

