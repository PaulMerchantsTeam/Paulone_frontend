package com.paulmerchants.gold.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class  GetPendingInrstDueRespItem(
    @SerializedName("ac_no") val ac_no: Long,
    @SerializedName("id") val id: Int,
    @SerializedName("created_at") val created_at: Long,
    @SerializedName("due_date") val due_date: String?,
    @SerializedName("rebate_amount") val rebate_amount: Int,
    @SerializedName("fine") val fine: Int,
    @SerializedName("interest_due") val interest_due: Int,
    @SerializedName("product_name") val product_name: String,
    @SerializedName("payable_amount") val payable_amount: Int?,  //!=0.0
    var currentDate: String,
) : Parcelable

/**
 * {"AcNo":102210000015198,"InterestDue":1652.0000,"ProductName":"SUGHAM LOAN  R","DueDate":"2024-06-21T00:00:00","Fine":0.0000,"RebateAmount":207.0000}
 */






