package com.paulmerchants.gold.model.other

import com.paulmerchants.gold.model.responsemodels.RespCustomersDetails

data class RespCustomCustomerDetail(
    val respGetCustomer: RespCustomersDetails?,
    val emailIdNew: String,
)
