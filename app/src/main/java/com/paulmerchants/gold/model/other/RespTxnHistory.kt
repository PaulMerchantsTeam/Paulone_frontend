package com.paulmerchants.gold.model.other

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.responsemodels.PagingRespTransactions

data class RespTxnHistory(
    @SerializedName("data") val data: PagingRespTransactions,
    @SerializedName("message") val message: String,
    @SerializedName("response_message") val response_message: String,
    @SerializedName("status") val status: String,
    @SerializedName("status_code") val status_code: Int?,
)














/**
 * {
"status": "SUCCESS",
"statusCode": "200",
"message": "Successfully Fetch All The Record",
"data": [
{
"id": 12,
"orderId": "order_NDnK6PEmzcEpWq",
"amount": 1892,
"receiptId": "PaulOne_B7lmXKx921GxR5",
"status": "PAID",
"custId": "182005482096",
"paymentId": "pay_NDnKKJ9GUS3nlZ",
"createdBy": 1,
"updatedBy": 1,
"createdAt": 1702899512000,
"updatedAt": 1702899530449,
"processFlag": 1,
"requestId": "2ADD83E55C61467D8520231218170832161"
},
{
"id": 8,
"orderId": "order_NDmoQXsh49yibo",
"amount": 1892,
"receiptId": "PaulOne_TzMcyD447qq6Bf",
"status": "PAID",
"custId": "182005482096",
"paymentId": "pay_NDmocOcaaJhUc3",
"createdBy": 1,
"updatedBy": 1,
"createdAt": 1702897713000,
"updatedAt": 1702897917851,
"processFlag": 1,
"requestId": "872C22D0CF6B40AFA020231218163832789"
}
],
"response_message": "Request Processed Successfully"
}
 */