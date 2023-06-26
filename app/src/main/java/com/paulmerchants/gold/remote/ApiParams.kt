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
    suspend fun getLogin(
        @Body login: RequestLogin,
    ): Response<RespLogin>

    @GET("Login/GetCustomer")
    suspend fun getCustomer(
        @Query("MobileNo") MobileNo: String,
    ): RespGetCustomer

    @GET("LoanDetails/GetLoanOutstanding")
    suspend fun getLoanOutstanding()

    @GET("LoanDetails/GetLoanDueDate")
    suspend fun getLoanDueDate()

    @GET("LoanDetails/GetPendingInterestDues")
    suspend fun getPendingInterestDues()

    @GET("LoanDetails/GetLoanClosureReceipt")
    suspend fun getLoanClosureReceipt()

    @GET("LoanDetails/GetReceipt ")
    suspend fun getReceipt()

    @GET("oanDetails/GetLoanStatement")
    suspend fun getLoanStatement()

    @GET("LoanDetails/GetCustomerDetails")
    suspend fun getCustomerDetails()

    @GET("LoanDetails/GetPaymentUpdate")
    suspend fun getPaymentUpdate()

    @GET("LoanDetails/GetRenewalEligibility")
    suspend fun getRenewalEligibility()

    @GET("LoanDetails/GetLoanRenewalProcess")
    suspend fun getLoanRenewalProcess()

}