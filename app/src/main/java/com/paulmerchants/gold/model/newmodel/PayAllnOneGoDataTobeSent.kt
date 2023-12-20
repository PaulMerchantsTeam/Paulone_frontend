package com.paulmerchants.gold.model.newmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayAllnOneGoDataTobeSent(
    val amount: Double,
    val payAll: List<PayAll>,
    val isFromAllGo: Boolean,
) : Parcelable