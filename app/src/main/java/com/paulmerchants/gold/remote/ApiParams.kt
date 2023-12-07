package com.paulmerchants.gold.remote

import com.paulmerchants.gold.model.ReqCustomerOtpNew
import com.paulmerchants.gold.model.ReqSetMPin
import com.paulmerchants.gold.model.RespSetMpin
import com.paulmerchants.gold.model.ResponseGetOtp
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.ReGetLoanClosureReceipNew
import com.paulmerchants.gold.model.newmodel.ReqCreateOrder
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespCutomerInfo
import okhttp3.ResponseBody
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiParams {


    @POST("auth/pml-login")
    suspend fun getLogin(
        @Body login: LoginReqNew,
    ): Response<LoginNewResp>

    @POST("api/CustomerInfo")
    suspend fun getCustomer(
        @Header("Authorization") Authorization: String,
        @Body reqCustomerNew: ReqCustomerNew,

        ): Response<RespCutomerInfo>   //RespGetCustomer

    @POST("otp/get-otp")
    suspend fun getOtp(
        @Header("Authorization") Authorization: String,
        @Body reqCustomerNew: ReqCustomerNew,
    ): Response<ResponseGetOtp>

    @POST("otp/validate-otp")
    suspend fun verifyOtp(
        @Header("Authorization") Authorization: String,
        @Body reqCustomerOtpNew: ReqCustomerOtpNew,
    ): Response<ResponseGetOtp>

    @POST("api/signup")
    suspend fun setMPin(
        @Header("Authorization") Authorization: String,
        @Body reqSetMPin: ReqSetMPin,
    ): Response<RespSetMpin>

    @POST("api/get-pending-interest-dues") // GetPendingInrstDueResp
    suspend fun getPendingInterestDues(
        @Header("Authorization") auth: String,
        @Body reqpendingInterstDueNew: ReqpendingInterstDueNew,
//        @Query("AsOnDate") AsOnDate: String,
    ): Response<*>


    @POST("api/get-loan-outstanding")   //RespGetLoanOutStanding
    suspend fun getLoanOutstanding(
        @Header("Authorization") auth: String,
        @Body reqpendingInterstDueNew: ReqpendingInterstDueNew,
    ): Response<RespCommon>

    @POST("api/get-loan-due-date")   //RespLoanDueDate
    suspend fun getLoanDueDate(
        @Header("Authorization") auth: String,
        @Query("Cust_ID") Cust_ID: String,
    ): ResponseBody

    @POST("payments/create-order")   //RespLoanDueDate
    suspend fun createOrder(
        @Header("Authorization") auth: String,
        @Body reqCreateOrder: ReqCreateOrder,
    ): Response<*>

    @POST("payments/payment/success/")   //RespLoanDueDate
    suspend fun updatePaymentStatus(
        @Header("Authorization") auth: String,
        @Query("status") status: String,
        @Query("razorpay_payment_id") razorpayPaymentId: String,
        @Query("razorpay_order_id") razorpayOrderId: String,
        @Query("razorpay_signature") razorpaySignature: String,
        @Query("custId") custId: String,
        @Path("amount") amount: Float,
        @Path("contactCount") contactCount: Int,
        @Path("companyName") companyName: String,
        @Path("currency") currency: String,
        @Path("description") description: String,
    ): Response<*>

    @GET("LoanDetails/GetLoanClosureReceipt") //RespClosureReceipt
    suspend fun getLoanClosureReceipt(
        @Header("Authorization") auth: String,
        @Body reqGetLoanClosureReceipNew: ReGetLoanClosureReceipNew,
    ): RespCommon

    @GET("LoanDetails/GetReceipt ") //RespGetReceipt
    suspend fun getReceipt(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
        @Query("VoucherNo") VoucherNo: String,
        @Query("TransID") TransID: String,
    ): RespCommon

    @GET("LoanDetails/GetLoanStatement") //RespLoanStatment
    suspend fun getLoanStatement(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
        @Query("FromDate") FromDate: String,
        @Query("ToDate") ToDate: String,
    ): RespCommon

    @GET("api/get-customer-details") //RespCustomersDetails
    suspend fun getCustomerDetails(
        @Header("Authorization") auth: String,
        @Body reqpendingInterstDueNew: ReqpendingInterstDueNew,
    ): RespCommon

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
    ): RespCommon


    @GET("LoanDetails/GetRenewalEligibility")  //RespRenewalEligiblity
    suspend fun getRenewalEligibility(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
    ): RespCommon

    @GET("LoanDetails/GetLoanRenewalProcess")   //RespLoanRenewalProcess
    suspend fun getLoanRenewalProcess(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
    ): RespCommon

}