package com.paulmerchants.gold.model.other

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.responsemodels.RespPaymentMethod

data class RespPaymentMethod(
    @SerializedName("data") val `data`: List<RespPaymentMethod>,
    @SerializedName("message") val message: String,
    @SerializedName("response_message") val response_message: String,
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val status_code: Int?,
)
/**
 * {
 *     "status": "SUCCESS",
 *     "statusCode": "200",
 *     "message": "Successfully Fetched All Payment Methods!",
 *     "data": [
 *         {
 *             "method": "UPI_COLLECT",
 *             "value": true
 *         },
 *         {
 *             "method": "UPI_INTENT",
 *             "value": true
 *         },
 *         {
 *             "method": "DebitCard",
 *             "value": true
 *         },
 *         {
 *             "method": "Netbanking",
 *             "value": true
 *         },
 *         {
 *             "method": "CreditCard",
 *             "value": true
 *         },
 *         {
 *             "method": "Wallet",
 *             "value": true
 *         }
 *     ],
 *     "response_message": "Request Processed Successfully"
 * }
 */