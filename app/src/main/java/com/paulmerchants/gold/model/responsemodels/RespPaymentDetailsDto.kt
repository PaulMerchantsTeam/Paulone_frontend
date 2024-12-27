package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class RespPaymentDetailsDto(
    @SerializedName("acquirer_data") val acquirer_data: AcquirerData?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("amount_refunded") val amount_refunded: Double?,
    @SerializedName("bank") val bank: String?,
    @SerializedName("base_amount") val base_amount: Double?,
    @SerializedName("captured") val captured: Boolean?,
    @SerializedName("card_id") val card_id: String?,
    @SerializedName("card") val card: Any?,
    @SerializedName("upi") val upi: Any?,
    @SerializedName("contact") val contact: String?,
    @SerializedName("created_at") val created_at: Long?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("entity") val entity: String?,
    @SerializedName("error_code") val error_code: String?,
    @SerializedName("error_description") val error_description: String?,
    @SerializedName("error_reason") val error_reason: String?,
    @SerializedName("error_source") val error_source: String?,
    @SerializedName("error_step") val error_step: String?,
    @SerializedName("fee") val fee: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("international") val international: Boolean?,
    @SerializedName("invoice_id") val invoice_id: String?,
    @SerializedName("method") val method: String?,
    @SerializedName("order_id") val order_id: String?,
    @SerializedName("payment_id") val payment_id: String?,
    @SerializedName("refund_status") val refund_status: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("tax") val tax: String?,
    @SerializedName("vpa") val vpa: String?,
    @SerializedName("wallet") val wallet: String?,
    @SerializedName("updated_at") val updated_at: Long?,
    @SerializedName("notes") val notes: List<String>?,




    )
