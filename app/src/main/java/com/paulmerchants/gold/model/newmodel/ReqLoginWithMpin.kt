package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class ReqLoginWithMpin(
  @SerializedName("mobile_no")  val mobile_no: String,
  @SerializedName("pin")  val pin: String
)