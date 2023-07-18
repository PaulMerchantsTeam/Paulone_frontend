package com.paulmerchants.gold.remote

import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespGetCustomer
import com.paulmerchants.gold.model.RespLogin
import okhttp3.ResponseBody

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiParams {

    @POST("Login/GetLogin")
    suspend fun getLogin(
        @Body login: RequestLogin,
    ): ResponseBody

    @GET("LoanDetails/GetCustomer")
    suspend fun getCustomer(
        @Header("Authorization") auth: String,
        @Query("MobileNo") MobileNo: String,
    ): ResponseBody   //RespGetCustomer

    @GET("LoanDetails/GetPendingInterestDues") // GetPendingInrstDueResp
    suspend fun getPendingInterestDues(
        @Header("Authorization") auth: String,
        @Query("Cust_ID") Cust_ID: String,
        @Query("AsOnDate") AsOnDate: String,
    ): ResponseBody


    @GET("LoanDetails/GetLoanOutstanding")   //RespGetLoanOutStanding
    suspend fun getLoanOutstanding(
        @Header("Authorization") auth: String,
        @Query("Cust_ID") Cust_ID: String,
    ): ResponseBody

    @GET("LoanDetails/GetLoanDueDate")   //RespLoanDueDate
    suspend fun getLoanDueDate(
        @Header("Authorization") auth: String,
        @Query("Cust_ID") Cust_ID: String,
    ): ResponseBody


    @GET("LoanDetails/GetLoanClosureReceipt") //RespClosureReceipt
    suspend fun getLoanClosureReceipt(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
    ): ResponseBody

    @GET("LoanDetails/GetReceipt ") //RespGetReceipt
    suspend fun getReceipt(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
        @Query("VoucherNo") VoucherNo: String,
        @Query("TransID") TransID: String,
    ): ResponseBody

    @GET("LoanDetails/GetLoanStatement") //RespLoanStatment
    suspend fun getLoanStatement(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
        @Query("FromDate") FromDate: String,
        @Query("ToDate") ToDate: String,
    ): ResponseBody

    @GET("LoanDetails/GetCustomerDetails") //RespCustomersDetails
    suspend fun getCustomerDetails(
        @Header("Authorization") auth: String,
        @Query("Cust_ID") Cust_ID: String,
    ): ResponseBody

    /**
     *̄----------ResPaymentDone     -----------------
     *̄----------RespPaymentNotDone-----------------
     *̄----------ErrorResp-----------------
     */

    @GET("LoanDetails/GetPaymentUpdate")
    suspend fun getPaymentUpdate(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
        @Query("IsSubmit") IsSubmit: String,
        @Query("InstAmt") InstAmt: String,
        @Query("ValueDate") ValueDate: String,
        @Query("makerID") makerID: String,
        @Query("mACID") mACID: String,
    ): ResponseBody


    @GET("LoanDetails/GetRenewalEligibility")  //RespRenewalEligiblity
    suspend fun getRenewalEligibility(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
    ): ResponseBody

    @GET("LoanDetails/GetLoanRenewalProcess")   //RespLoanRenewalProcess
    suspend fun getLoanRenewalProcess(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
    ): ResponseBody

}