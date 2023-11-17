package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.newmodel.DeviceDetailsDTO

data class LoginReqNew(
   @SerializedName("deviceDetailsDTO") val deviceDetailsDTO: DeviceDetailsDTO,
   @SerializedName("password") val password: String,
   @SerializedName("userName") val userName: String
)