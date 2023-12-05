package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.newmodel.DeviceDetailsDTO

data class ReqSetMPin(
   @SerializedName("confirmMPin") val confirmMPin: String,
   @SerializedName("deviceDetailsDTO") val deviceDetailsDTO: DeviceDetailsDTO,
   @SerializedName("emailId") val emailId: String,
   @SerializedName("fullName") val fullName: String,
   @SerializedName("mobileNo") val mobileNo: String,
   @SerializedName("setUpMPin") val setUpMPin: String,
   @SerializedName("termsAndCondition") val termsAndCondition: Boolean
)