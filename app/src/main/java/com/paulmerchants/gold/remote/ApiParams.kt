package com.paulmerchants.gold.remote

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiParams {

    @GET("is-down")
    suspend fun isUnderMaintenance(): Response<ResponseBody>

    @POST("otp/send")
    suspend fun getOtp(
        @Body data: RequestBody,
    ): Response<ResponseBody>

    @POST("otp/validate")
    suspend fun verifyOtp(
        @Header("Authorization") Authorization: String?,
        @Body data: RequestBody
    ): Response<ResponseBody>

    @POST("mpin/signup")
    suspend fun setMPin(
        @Header("Authorization") Authorization: String?,
        @Body requestBody: RequestBody,
    ): Response<ResponseBody>

    @POST("mpin/login")
    suspend fun loginWithMpin(
        @Body requestBody: RequestBody,
    ): Response<ResponseBody>

    @POST("mpin/forget")
    suspend fun resetOrForgetMpin(
        @Header("Authorization") Authorization: String?,
        @Body requestBody: RequestBody,
    ): Response<ResponseBody>

    @POST("mpin/change") //change Mpin
    suspend fun reSetMPin(
        @Header("Authorization") Authorization: String,
        @Body requestBody: RequestBody,
    ): Response<ResponseBody>

    @POST("api/pending-interest-dues") // GetPendingInrstDueResp
    suspend fun getPendingInterestDues(
        @Header("Authorization") auth: String,
        @Body requestBody: RequestBody,
//        @Query("AsOnDate") AsOnDate: String,
    ): Response<ResponseBody>

    @POST("api/loan-outstanding")   //RespGetLoanOutStanding
    suspend fun getLoanOutstanding(
        @Header("Authorization") auth: String,
        @Body requestBody: RequestBody,
    ): Response<ResponseBody>

    @POST("api/customer-details") //RespCustomersDetails
    suspend fun getCustomerDetails(
        @Header("Authorization") auth: String,
        @Body requestBody: RequestBody,
    ): Response<ResponseBody>

    @GET("payment/methods-status")   //RespLoanDueDate
    suspend fun getPaymentMethod(
        @Header("Authorization") auth: String,
    ): Response<ResponseBody>

    @POST("payment/create-order")   //RespLoanDueDate
    suspend fun createOrder(
        @Header("Authorization") auth: String,
        @Body requestBody: RequestBody,
    ): Response<ResponseBody>

    @POST("payment/payment-confirmation")   //RespLoanDueDate
    suspend fun mTransaction(
        @Header("Authorization") auth: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>

    @GET("branches")   //
    suspend fun fetchAllBranch(
        @Header("Authorization") auth: String,
        @Query("page_number") page_number: Int,
        @Query("page_size") page_size: Int,
        @Query("sort_by") sort_by: String = "branchId",
        @Query("sort_dir") sort_dir: String = "asc",
    ): ResponseBody

    @GET("branches/search-by-branch-name")   //
    suspend fun searchByBranchName(
        @Header("Authorization") auth: String,
        @Query("branch_name") branch_name: String,
        @Query("page_number") page_number: Int,
        @Query("page_size") page_size: Int,
        @Query("sort_by") sort_by: String = "branchName",
        @Query("sort_dir") sort_dir: String = "ASC",
    ): ResponseBody


    @POST("payment/transaction-history")   //
    suspend fun txnHistory(
        @Header("Authorization") auth: String,
        @Query("data") data: String?,
        @Query("page_number") page_number: Int,
        @Query("page_size") page_size: Int,
    ): ResponseBody

    @POST("payment/transaction-history-search")   //
    suspend fun txnHistorySearch(
        @Header("Authorization") auth: String,
        @Query("cust_id") cust_id: String?,
        @Query("status") status: String?,
        @Query("page_number") page_number: Int,
        @Query("page_size") page_size: Int,
    ): ResponseBody

    @GET("payment/transaction-receipt")   //
    suspend fun getPaidReceipt(
        @Header("Authorization") auth: String,
        @Query("order_id") order_id: String? = "",
        @Query("payment_id") payment_id: String? = "",
    ): Response<ResponseBody>

    @POST("auth/logout")
    suspend fun logOut(
        @Header("Authorization") Authorization: String,
    ): Response<ResponseBody>

    @POST("auth/refresh-token/v1")
    suspend fun refreshToken(
       @Body requestBody: RequestBody,
    ): Response<ResponseBody>

}
