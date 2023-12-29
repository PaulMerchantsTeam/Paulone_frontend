package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class RespSearchBranch(
    @SerializedName("data") val `data`: List<PmlBranch>,
    @SerializedName("lastPage") val lastPage: Boolean,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalRecords") val totalRecords: Int,
    @SerializedName("message") val message: String,
    @SerializedName("response_message") val response_message: String,
    @SerializedName("status") val status: String,
    @SerializedName("statusCode") val statusCode: String,
)