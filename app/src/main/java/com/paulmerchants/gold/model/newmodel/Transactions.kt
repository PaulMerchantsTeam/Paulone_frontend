package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class Transactions(
    @SerializedName("amount") val amount: Int?,
    @SerializedName("created_at") val created_at: Long,
    @SerializedName("created_by") val created_by: Int?,
    @SerializedName("updated_by") val updated_by: Int?,
    @SerializedName("cust_id") val cust_id: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("order_id") val order_id: String?,
    @SerializedName("payment_id") val payment_id: String?,
    @SerializedName("process_flag") val process_flag: Int?,
    @SerializedName("receipt_id") val receipt_id: String?,
    @SerializedName("request_id") val request_id: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("update_at") val update_at: Long?,
    @SerializedName("acc_no") val acc_no: String?,
    @SerializedName("submit") val submit: Boolean?,
    @SerializedName("value_date") val value_date: String?,
    @SerializedName("maker_id") val maker_id: String?,
    @SerializedName("mac_id") val mac_id: String?,



    )





