package com.paulmerchants.gold.model

data class TransDoneModel(
    val transId: Int,
    val transImage: Int,
    val transType: Int,
    val transTitle: String,
    val amountDone: String,
    val dateDone: String
)


