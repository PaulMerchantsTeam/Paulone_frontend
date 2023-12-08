package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class ReqComplaintRaise(
   @SerializedName("complaintType") val complaintType: String,
   @SerializedName("subComplaintTypeDTO") val subComplaintTypeDTO: List<SubComplaintTypeDTO>
)