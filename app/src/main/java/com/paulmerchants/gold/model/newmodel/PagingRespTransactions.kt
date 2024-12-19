package com.paulmerchants.gold.model.newmodel

import com.google.gson.annotations.SerializedName

data class PagingRespTransactions(

    @SerializedName("data") val `data`: List<Transactions>?,
    @SerializedName("is_last_page") val is_last_page: Boolean?,
    @SerializedName("page_number") val page_number: Int?,
    @SerializedName("page_size") val page_size: Int?,
    @SerializedName("total_pages") val total_pages: Int?,
    @SerializedName("total_records") val total_records: Int?

)
