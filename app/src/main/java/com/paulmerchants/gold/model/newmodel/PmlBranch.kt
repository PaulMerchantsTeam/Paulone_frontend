package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class PmlBranch(
    @SerializedName("branchAddress") val branchAddress: String,
    @SerializedName("branchCity") val branchCity: String,
    @SerializedName("branchId") val branchId: Int,
    @SerializedName("branchLat") val branchLat: String,
    @SerializedName("branchLng") val branchLng: String,
    @SerializedName("branchName") val branchName: String,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("updatedAt") val updatedAt: Long,
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