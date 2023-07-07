package com.paulmerchants.gold.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetPendingInrstDueRespItem(
    @SerializedName("AcNo") val AcNo: Long,
    @SerializedName("DueDate") val DueDate: String,
    @SerializedName("Fine") val Fine: Double,
    @SerializedName("InterestDue") val InterestDue: Double,
    @SerializedName("ProductName") val ProductName: String,
) : Parcelable