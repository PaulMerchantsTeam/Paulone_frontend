package com.paulmerchants.gold.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RespGetLoanOutStandingItem(
    @SerializedName("AcNo") val AcNo: Long,
    @SerializedName("ClosedDate") val ClosedDate: String?,
    @SerializedName("DueDate") val DueDate: String,
    @SerializedName("Fine") val Fine: Int,
    @SerializedName("InterestPeriod") val InterestPeriod: Int,
    @SerializedName("IsClosed") val IsClosed: Boolean?,
    @SerializedName("OpenDate") val OpenDate: String,
    @SerializedName("OpeningAmount") val OpeningAmount: Int?,
    @SerializedName("OutStanding") val OutStanding: Int?,
    @SerializedName("ProductName") val ProductName: String,
) : Parcelable

/**
 *  [RespGetLoanOutStandingItem(AcNo=104210000012072,
 *  ClosedDate=null, DueDate=2024-06-20T00:00:00, Fine=0, InterestPeriod=0,
 *  IsClosed=false, OpenDate=2023-06-20T00:00:00, OpeningAmount=100000,
 *  OutStanding=100000, ProductName=PFLS 18  R)]
 */