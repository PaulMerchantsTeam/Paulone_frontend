package com.paulmerchants.gold.remote

import com.itextpdf.text.PageSize
import com.paulmerchants.gold.model.ReqCustomerOtpNew
import com.paulmerchants.gold.model.ReqSetMPin
import com.paulmerchants.gold.model.RespSetMpin
import com.paulmerchants.gold.model.ResponseGetOtp
import com.paulmerchants.gold.model.ResponseVerifyOtp
import com.paulmerchants.gold.model.newmodel.DeviceDetailsDTO
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.ReGetLoanClosureReceipNew
import com.paulmerchants.gold.model.newmodel.ReqComplaintRaise
import com.paulmerchants.gold.model.newmodel.ReqCreateOrder
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.model.newmodel.ReqGetLoanStatement
import com.paulmerchants.gold.model.newmodel.ReqLoginWithMpin
import com.paulmerchants.gold.model.newmodel.ReqPayAlInOnGo
import com.paulmerchants.gold.model.newmodel.ReqResetForgetPin
import com.paulmerchants.gold.model.newmodel.ReqResetPin
import com.paulmerchants.gold.model.newmodel.ReqpendingInterstDueNew
import com.paulmerchants.gold.model.newmodel.RespAllBranch
import com.paulmerchants.gold.model.newmodel.RespCommon
import com.paulmerchants.gold.model.newmodel.RespCutomerInfo
import com.paulmerchants.gold.model.newmodel.RespLoginWithMpin
import com.paulmerchants.gold.model.newmodel.RespResetFogetMpin
import com.paulmerchants.gold.model.newmodel.RespSearchBranch
import com.paulmerchants.gold.model.newmodel.RespTxnHistory
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

    @POST("api/login_mpin")
    suspend fun loginWithMpin(
        @Header("Authorization") Authorization: String,
        @Body login: ReqLoginWithMpin,
    ): Response<RespLoginWithMpin>


    @POST("auth/logout")
    suspend fun logOut(
        @Header("Authorization") Authorization: String,
    ): Response<RespCommon>

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
    ): Response<ResponseVerifyOtp>

    /**
     * {"status":"SUCCESS","statusCode":"200","message":"Successfully Validate Your OTP: 9196",
     * "userExist":true,"data":false,"response_message":"Request Processed Successfully"}
     */

    @POST("api/signup")
    suspend fun setMPin(
        @Header("Authorization") Authorization: String,
        @Body reqSetMPin: ReqSetMPin,
    ): Response<RespSetMpin>

    @POST("api/reset-mpin") //change Mpin
    suspend fun reSetMPin(
        @Header("Authorization") Authorization: String,
        @Body reqResetPin: ReqResetPin,
    ): Response<RespCommon>

    @POST("api/forget-mpin")
    suspend fun resetOrForgetMpin(
        @Header("Authorization") Authorization: String,
        @Body reqResetPin: ReqResetForgetPin,
    ): Response<RespResetFogetMpin>


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

    //https://www.globedu.in/paulgoldv01/
    @POST("payments/payment/success/{amount}/{contactCount}/{companyName}/{currency}/{description}")   //RespLoanDueDate
    suspend fun updatePaymentStatus(
        @Header("Authorization") auth: String,
        @Path("amount") amount: Double?,
        @Path("contactCount") contactCount: Int,
        @Path("companyName") companyName: String,
        @Path("currency") currency: String,
        @Path("description") description: String,
        @Query("status") status: String,
        @Query("razorpay_payment_id") razorpayPaymentId: String,
        @Query("razorpay_order_id") razorpayOrderId: String,
        @Query("razorpay_signature") razorpaySignature: String,
        @Query("custId") custId: String,
        @Query("acNo") acNo: String,
        @Query("makerId") makerId: String,
        @Query("macID") macID: String,
        @Query("isCustom") isCustom: Boolean,
        @Body deviceDetailsDTO: DeviceDetailsDTO,
    ): Response<*>

    @POST("payments/payment/{amount}/{contactCount}/{companyName}/{currency}/{description}")   //RespLoanDueDate
    suspend fun updatePaymentStatusAllInOneGo(
        @Header("Authorization") auth: String,
        @Path("amount") amount: Double?,
        @Path("contactCount") contactCount: Int,
        @Path("companyName") companyName: String,
        @Path("currency") currency: String,
        @Path("description") description: String,
        @Query("status") status: String,
        @Query("razorpay_payment_id") razorpayPaymentId: String,
        @Query("razorpay_order_id") razorpayOrderId: String,
        @Query("razorpay_signature") razorpaySignature: String,
        @Query("custId") custId: String,
        @Query("makerId") makerId: String,
        @Query("macID") macID: String,
        @Body payAllInOnGo: ReqPayAlInOnGo,
    ): Response<*>

    /**
     *    @RequestParam("acNo") String acNo,
    @RequestParam("makerId") String makerId,
    @RequestParam("macID") String macID,
     */

    @GET("api/get-loan-closure-receipt") //RespClosureReceipt
    suspend fun getLoanClosureReceipt(
        @Header("Authorization") auth: String,
        @Body reqGetLoanClosureReceipNew: ReGetLoanClosureReceipNew,
    ): Response<RespCommon>

    @GET("LoanDetails/GetReceipt ") //RespGetReceipt
    suspend fun getReceipt(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
        @Query("VoucherNo") VoucherNo: String,
        @Query("TransID") TransID: String,
    ): Response<RespCommon>

    @POST("api/get-loan-statements") //RespLoanStatment
    suspend fun getLoanStatement(
        @Header("Authorization") auth: String,
        @Body reqGetLoanStatement: ReqGetLoanStatement,
    ): Response<RespCommon>

    @POST("api/get-customer-details") //RespCustomersDetails
    suspend fun getCustomerDetails(
        @Header("Authorization") auth: String,
        @Body reqpendingInterstDueNew: ReqpendingInterstDueNew,
    ): Response<RespCommon>

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
    ): Response<RespCommon>


    @GET("LoanDetails/GetRenewalEligibility")  //RespRenewalEligiblity
    suspend fun getRenewalEligibility(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
    ): Response<RespCommon>

    @GET("LoanDetails/GetLoanRenewalProcess")   //RespLoanRenewalProcess
    suspend fun getLoanRenewalProcess(
        @Header("Authorization") auth: String,
        @Query("AcNo") AcNo: String,
    ): Response<RespCommon>

    @GET("complaint/create")   //RespLoanRenewalProcess
    suspend fun raiseAComplaint(
        @Header("Authorization") auth: String,
        @Body reqComplaintRaise: ReqComplaintRaise,
    ): Response<RespCommon>

    @GET("complaint/getAllComplaints")   //
    suspend fun getAllComplaint(
        @Header("Authorization") auth: String,
    ): Response<RespCommon>

    @GET("branch/all")   //
    suspend fun fetchAllBranch(
        @Header("Authorization") auth: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("sortBy") sortBy: String = "branchId",
        @Query("sortDir") sortDir: String = "asc",
    ): Response<RespAllBranch>

    @GET("branch/search-by-branch-name/{branchName}")   //
    suspend fun searchByBranchName(
        @Header("Authorization") auth: String,
        @Path("branchName") branchName: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("sortBy") sortBy: String = "branchName",
        @Query("sortDir") sortDir: String = "asc",
    ): Response<RespSearchBranch>


    @POST("payments/transaction-history/{custId}")   //
    suspend fun txnHistory(
        @Header("Authorization") auth: String,
        @Path("custId") custId: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
    ): Response<RespTxnHistory>

    @GET("/branch/search-by-branch-name/Paul Merchants")   //
    suspend fun searchBranch(
        @Header("Authorization") auth: String,
        @Query("pageNumber") pageNumber: String,
        @Query("pageSize") pageSize: String,
        @Query("sortBy") sortBy: String,
        @Query("sortDir") sortDir: String,
    ): Response<RespCommon>

}