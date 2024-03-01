package com.paulmerchants.gold.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetPendingInrstDueRespItem(
    @SerializedName("acNo") val acNo: Long,
    @SerializedName("dueDate") val dueDate: String?,
    @SerializedName("rebateAmount") val rebateAmount: Double,
    @SerializedName("fine") val fine: Double,
    @SerializedName("interestDue") val interestDue: Double,
    @SerializedName("productName") val productName: String,
    @SerializedName("payableAmount") val payableAmount: Double?,  //!=0.0
    var currentDate: String,
) : Parcelable

/**
 * {"AcNo":102210000015198,"InterestDue":1652.0000,"ProductName":"SUGHAM LOAN  R","DueDate":"2024-06-21T00:00:00","Fine":0.0000,"RebateAmount":207.0000}
 */