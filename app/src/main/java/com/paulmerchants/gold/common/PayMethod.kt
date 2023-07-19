package com.paulmerchants.gold.common

enum class PayMethod(val id: Int) {
    PREPAID_CARD(1),
    BHIM(2),
    PAYTM(3),
    GPAY(4),
    PHONE_PAY(5),
    DEBIT_CARD(6),
    CREDIT_CARD(7)
}