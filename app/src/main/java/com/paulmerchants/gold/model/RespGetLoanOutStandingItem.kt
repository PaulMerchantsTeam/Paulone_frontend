package com.paulmerchants.gold.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class RespGetLoanOutStandingItem(
    @SerializedName("ac_no") val ac_no: Long,
    @SerializedName("closed_date") val closed_date: String?,
    @SerializedName("due_date") val due_date: String,
    @SerializedName("open_date") val open_date: String,
    @SerializedName("interest_due") val interest_due: Int?,
    @SerializedName("product_name") val product_name: String,
    @SerializedName("openDate") val openDate: String,
    @SerializedName("interest_period") val interest_period: Int,
    @SerializedName("fine") val fine: Int,
    @SerializedName("is_closed") val is_closed: Boolean?,
    @SerializedName("opening_amount") val opening_amount: Int?,
    @SerializedName("out_standing") val out_standing: Int?,
    @SerializedName("rebate_amount") val rebate_amount: Int?,
    @SerializedName("payable_amount") val payable_amount: Double?,
    @SerializedName("created_at") val created_at: Long?,
    @SerializedName("id") val id: Int?,
    var current_date: String,
) : Parcelable






/**
 *  [RespGetLoanOutStandingItem(AcNo=104210000012072,
 *  ClosedDate=null, DueDate=2024-06-20T00:00:00, Fine=0, InterestPeriod=0,
 *  IsClosed=false, OpenDate=2023-06-20T00:00:00, OpeningAmount=100000,
 *  OutStanding=100000, ProductName=PFLS 18  R)]
 */