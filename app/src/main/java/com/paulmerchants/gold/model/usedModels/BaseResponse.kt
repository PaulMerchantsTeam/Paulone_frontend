package com.paulmerchants.gold.model.usedModels

import com.google.gson.annotations.SerializedName

data class BaseResponse<M>(
  @SerializedName("data")  val data: M?,
  @SerializedName("message")  val message: String?,
  @SerializedName("response_message")  val response_message: String?,
  @SerializedName("status")  val status: String?,
  @SerializedName("status_code")  val status_code: Int?
)