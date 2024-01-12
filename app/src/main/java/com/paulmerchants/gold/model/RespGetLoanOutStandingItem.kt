package com.paulmerchants.gold.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class RespGetLoanOutStandingItem(
    @SerializedName("acNo") val AcNo: Long,
    @SerializedName("closedDate") val closedDate: String?,
    @SerializedName("dueDate") val dueDate: String,
    @SerializedName("interestDue") val interestDue: Double?,
    @SerializedName("productName") val productName: String,
    @SerializedName("openDate") val openDate: String,
    @SerializedName("interestPeriod") val interestPeriod: Int,
    @SerializedName("fine") val fine: Int,
    @SerializedName("closed") val closed: Boolean?,
    @SerializedName("openingAmount") val openingAmount: Double?,
    @SerializedName("outStanding") val outStanding: Double?,
    @SerializedName("rebateAmount") val rebateAmount: Double?,
    @SerializedName("payableAmount") val payableAmount: Double?,
    var currentDate: String,
) : Parcelable

/**
 *  [RespGetLoanOutStandingItem(AcNo=104210000012072,
 *  ClosedDate=null, DueDate=2024-06-20T00:00:00, Fine=0, InterestPeriod=0,
 *  IsClosed=false, OpenDate=2023-06-20T00:00:00, OpeningAmount=100000,
 *  OutStanding=100000, ProductName=PFLS 18  R)]
 */