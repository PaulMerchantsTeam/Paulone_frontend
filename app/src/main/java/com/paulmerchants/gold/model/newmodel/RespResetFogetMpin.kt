package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class RespResetFogetMpin(
   @SerializedName("data") val `data`: DataX,
   @SerializedName("message") val message: String,
   @SerializedName("response_message") val response_message: String,
   @SerializedName("status") val status: String,
   @SerializedName("statusCode") val statusCode: String,
   @SerializedName("userExist") val userExist: Boolean
)

/**
 * {"status":"SUCCESS","statusCode":"200","message":"Successfully Validate Your OTP: 2035",
 * "userExist":true,"data":{"mobileNo":"8789782803","otp":"2035"},
 * "response_message":"Request Processed Successfully"}
 */