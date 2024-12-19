package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.usedModels.DeviceDetailsDTO

data class RequestMTransaction(
    @SerializedName("ac_no") val ac_no: String?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("cust_id") val cust_id: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("device_details_dto") val device_details_dto: DeviceDetailsDTO?,
    @SerializedName("mac_id") val mac_id: String?,
    @SerializedName("maker_id") val maker_id: String?,
    @SerializedName("razorpay_order_id") val razorpay_order_id: String?,
    @SerializedName("razorpay_payment_id") val razorpay_payment_id: String?,
    @SerializedName("razorpay_signature") val razorpay_signature: String?,
    @SerializedName("status") val status: String?
)