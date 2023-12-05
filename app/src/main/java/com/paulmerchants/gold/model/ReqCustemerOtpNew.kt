package com.paulmerchants.gold.model

import com.google.gson.annotations.SerializedName
import com.paulmerchants.gold.model.newmodel.DeviceDetailsDTO


data class ReqCustomerOtpNew(
        @SerializedName("mobileNo") val  mobileNo: String,
        @SerializedName("otp") val  otp: String,
        @SerializedName("deviceDetailsDTO") val deviceDetailsDTO: DeviceDetailsDTO,
)
