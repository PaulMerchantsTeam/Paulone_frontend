package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class ReqLoginWithMpin(
  @SerializedName("mobileNo")  val mobileNo: String,
  @SerializedName("pin")  val pin: String
)