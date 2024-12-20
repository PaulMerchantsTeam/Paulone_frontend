package com.paulmerchants.gold.model.other

import android.os.Parcelable
import com.paulmerchants.gold.model.other.PayAll
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayAllnOneGoDataTobeSent(
    val amount: Double,
    val payAll: List<PayAll>,
    val isFromAllGo: Boolean,
) : Parcelable