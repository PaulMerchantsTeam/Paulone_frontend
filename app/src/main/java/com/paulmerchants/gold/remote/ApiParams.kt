package com.paulmerchants.gold.remote

import retrofit2.http.GET
import retrofit2.http.POST

interface ApiParams {

    @POST("Login/GetLogin")
    fun getLogin()

    @GET("Login/GetCustomer")
    fun getCustomer()

    @GET("LoanDetails/GetLoanOutstanding")
    fun getLoanOutstanding()

    @GET("LoanDetails/GetLoanDueDate")
    fun getLoanDueDate()

    @GET("LoanDetails/GetPendingInterestDues")
    fun getPendingInterestDues()

    @GET("LoanDetails/GetLoanClosureReceipt")
    fun getLoanClosureReceipt()

    @GET("LoanDetails/GetReceipt ")
    fun getReceipt()

    @GET("oanDetails/GetLoanStatement")
    fun getLoanStatement()

    @GET("LoanDetails/GetCustomerDetails")
    fun getCustomerDetails()

    @GET("LoanDetails/GetPaymentUpdate")
    fun getPaymentUpdate()

    @GET("LoanDetails/GetRenewalEligibility")
    fun getRenewalEligibility()

    @GET("LoanDetails/GetLoanRenewalProcess")
    fun getLoanRenewalProcess()

}