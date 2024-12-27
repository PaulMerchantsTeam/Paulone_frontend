package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class BaseResponse<M>(
  @SerializedName("data")  val data: M? = null,
  @SerializedName("message")  val message: String?,
  @SerializedName("response_message")  val response_message: String?,
  @SerializedName("status")  val status: String?,
  @SerializedName("status_code")  val status_code: Int?,
  @SerializedName("response_type")  val response_type: String?
)
