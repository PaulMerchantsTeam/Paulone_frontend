package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class EntityPayment(
    @SerializedName("acquirer_data") val acquirer_data: AcquirerData?,
    @SerializedName("amount") val amount: Int?,
    @SerializedName("amount_refunded") val amount_refunded: Int?,
    @SerializedName("bank") val bank: String?,
    @SerializedName("base_amount") val base_amount: Int?,
    @SerializedName("captured") val captured: Boolean?,
    @SerializedName("card_id") val card_id: String?,
    @SerializedName("contact") val contact: String?,
    @SerializedName("created_at") val created_at: String?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("entity") val entity: String?,
    @SerializedName("error_code") val error_code: String?,
    @SerializedName("error_description") val error_description: String?,
    @SerializedName("error_reason") val error_reason: String?,
    @SerializedName("error_source") val error_source: String?,
    @SerializedName("error_step") val error_step: String?,
    @SerializedName("fee") val fee: Int?,
    @SerializedName("id") val id: Int?,
    @SerializedName("international") val international: Boolean?,
    @SerializedName("invoice_id") val invoice_id: String?,
    @SerializedName("method") val method: String?,
    @SerializedName("order_id") val order_id: String?,
    @SerializedName("paymentId") val paymentId: String?,
    @SerializedName("refund_status") val refund_status: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("tax") val tax: Int?,
    @SerializedName("vpa") val vpa: String?,
    @SerializedName("wallet") val wallet: String?,
)