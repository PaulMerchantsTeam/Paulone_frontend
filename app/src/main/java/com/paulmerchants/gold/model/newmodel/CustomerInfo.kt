package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class CustomerInfo(
  @SerializedName("created_at") val created_at: Any?,
  @SerializedName("cust_id") val cust_id: String?,
  @SerializedName("cust_name") val cust_name: String?,
  @SerializedName("id") val id: Int?,
  @SerializedName("mobile_no") val mobile_no: String?,
  @SerializedName("response_time") val response_time: Long?,
  @SerializedName("status") val status: String?,
  @SerializedName("updated_at") val updated_at: Long?
)