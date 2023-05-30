package com.paulmerchants.gold.remote

import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespGetCustomer
import com.paulmerchants.gold.model.RespLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiParams {

    @POST("Login/GetLogin")
    fun getLogin(
        @Body login: RequestLogin
    ): Response<RespLogin>

    @GET("Login/GetCustomer")
    fun getCustomer(
        @Query("MobileNo") MobileNo: String
    ): RespGetCustomer

    @GET("LoanDetails/GetLoanOutstanding")
    fun getLoanOutstanding()

    @GET("LoanDetails/GetLoanDueDate")F
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