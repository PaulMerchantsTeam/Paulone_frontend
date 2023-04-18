package com.paulmerchants.gold.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OurServices(
    val serviceImage: Int,
    val serviceName : String,

) : Parcelable