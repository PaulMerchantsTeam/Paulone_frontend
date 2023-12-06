package com.paulmerchants.gold.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetPendingInrstDueRespItem(
    @SerializedName("AcNo") val AcNo: Long,
    @SerializedName("DueDate") val DueDate: String,
    @SerializedName("RebateAmount") val RebateAmount: Double,
    @SerializedName("Fine") val Fine: Double,
    @SerializedName("InterestDue") val InterestDue: Double,
    @SerializedName("ProductName") val ProductName: String,
) : Parcelable

/**
 * {"AcNo":102210000015198,"InterestDue":1652.0000,"ProductName":"SUGHAM LOAN  R","DueDate":"2024-06-21T00:00:00","Fine":0.0000,"RebateAmount":207.0000}
 */