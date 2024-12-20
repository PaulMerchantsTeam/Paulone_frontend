package com.paulmerchants.gold.model.responsemodels

import com.google.gson.annotations.SerializedName

data class PmlBranch(
   @SerializedName("branch_address") val branch_address: String?,
   @SerializedName("branch_city") val branch_city: String?,
   @SerializedName("branch_id") val branch_id: Int?,
   @SerializedName("branch_lat") val branch_lat: String?,
   @SerializedName("branch_lng") val branch_lng: String?,
   @SerializedName("branch_name") val branch_name: String?,
   @SerializedName("created_at") val created_at: Long?,
   @SerializedName("updated_at") val updated_at: Long?
)

/**
 *  {
"branchId": 2,
"branchName": "Paul Merchants\n,Zirakpur",
"branchLat": "30.737825",
"branchLng": "76.775396",
"branchAddress": "Paul Merchants, Sco No 3-4, Ground Floor, Shree Balaji, Complex, Patiala Raod, Zirakpur-140606",
"branchCity": "pb",
"createdAt": 1702901412610,
"updatedAt": 1702901412610
},
 */