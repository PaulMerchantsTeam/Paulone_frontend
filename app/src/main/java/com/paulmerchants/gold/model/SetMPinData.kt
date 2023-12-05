package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.newmodel.DeviceDetailsDTO

data class SetMPinData(
  @SerializedName("confirmMPin") val confirmMPin: String,
  @SerializedName("createdAt") val createdAt: Long,
  @SerializedName("deviceDetails") val deviceDetails: DeviceDetailsDTO,
  @SerializedName("emailId") val emailId: String,
  @SerializedName("fullName") val fullName: String,
  @SerializedName("id") val id: Int,
  @SerializedName("mobileNo") val mobileNo: String,
  @SerializedName("termsAndCondition") val termsAndCondition: Boolean
)