package com.paulmerchants.gold.model.newmodel

data class GetpendingRespNew(
    val `data`: GepPendingRespObj,
    val message: String,
    val response_message: String,
    val status: String,
    val statusCode: String
)