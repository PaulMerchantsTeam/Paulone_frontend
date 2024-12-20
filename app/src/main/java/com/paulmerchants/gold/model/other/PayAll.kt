package com.paulmerchants.gold.model.other

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayAll(
    @SerializedName("accountNo") val accountNo: String,
    @SerializedName("amount") val amount: Double?,
) : Parcelable