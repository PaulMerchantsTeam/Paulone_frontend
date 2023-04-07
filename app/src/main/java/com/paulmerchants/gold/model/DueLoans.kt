package com.paulmerchants.gold.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DueLoans(
    val payId: Int,
    val dueDays: Int,
    val amount: Int,
) : Parcelable
