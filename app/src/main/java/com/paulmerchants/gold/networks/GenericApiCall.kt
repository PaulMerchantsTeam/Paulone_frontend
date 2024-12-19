package com.paulmerchants.gold.networks

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.usedModels.BaseResponse
import com.paulmerchants.gold.utility.AppUtility.hideProgressBar
import com.paulmerchants.gold.utility.AppUtility.progressBarAlert
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody


inline fun <reified T> callApiGeneric(
    progress: Boolean,
    context: Context,
    request: Any? = null,
    crossinline apiCall: suspend (RequestBody) -> ResponseBody,
    crossinline onSuccess: (BaseResponse<T>) -> Unit,
    crossinline onClientError: (Int, String) -> Unit,
    crossinline onServerError: (Int, String) -> Unit,
    crossinline onUnexpectedError: (String) -> Unit,
    crossinline onError: (String) -> Unit
) {
    val coRoutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        CoroutineScope(Dispatchers.Main).launch {
            throwable.message.let { onError(it ?: "") }
        }
    }
    CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler + Job()).launch {
        withContext(Dispatchers.Main) {
            if (progress) {
                progressBarAlert(context)
            }

        }
        try {
            // Convert request to JSON and encrypt
            val gson = Gson()


            val requestBody = request?.let {
                val jsonString = gson.toJson(it)
                val encryptedRequest = encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString)  ?: jsonString
                encryptedRequest.toRequestBody("text/plain".toMediaTypeOrNull())
            }
            // Make API call
            val response = requestBody?.let { apiCall(it) }
            val plainTextResponse = response?.string()

            // Decrypt the response
            val decryptedResponse = decryptKey(BuildConfig.SECRET_KEY_UAT, plainTextResponse)

            // Parse the response generically
            val typeToken = object : TypeToken<BaseResponse<T>>() {}
            val parsedResponse: BaseResponse<T> = gson.fromJson(decryptedResponse, typeToken.type)
            when (parsedResponse.status_code) {
                200 -> {
                    hideProgressBar()
                    // Success
                    onSuccess(parsedResponse)
                }

                400, 401, 498 -> {
                    hideProgressBar()
                    // Client errors
                    onClientError(
                        parsedResponse.status_code,
                        parsedResponse.message ?: "Client error"
                    )

                }

                500 -> {
                    hideProgressBar()
                    // Server error
                    onServerError(
                        parsedResponse.status_code,
                        parsedResponse.message ?: "Server error"
                    )


                }

                else -> {
                    hideProgressBar()
                    // Unexpected or unhandled status codes
                    onUnexpectedError(parsedResponse.message ?: "An Unexpected error occurred")

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            hideProgressBar()
            onUnexpectedError(e.localizedMessage ?: "An exception occurred")
        } finally {
            hideProgressBar()
        }
    }
}




