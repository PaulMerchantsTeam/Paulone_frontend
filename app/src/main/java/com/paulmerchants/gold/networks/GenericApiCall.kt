import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.responsemodels.BaseResponse
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
import retrofit2.Response

inline fun <reified T> callApiGeneric(
    progress: Boolean,
    context: Context,
    request: Any? = null,
    crossinline apiCall: suspend (RequestBody) -> Response<ResponseBody>,  // This now returns Response<ResponseBody>
    crossinline onSuccess: (BaseResponse<T>) -> Unit,
    crossinline onTokenExpired: (BaseResponse<T>) -> Unit,
    crossinline onClientError: (Int, String) -> Unit,
    crossinline onUnexpectedError: (String) -> Unit
) {
    val coRoutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        CoroutineScope(Dispatchers.Main).launch {
            throwable.message?.let { onUnexpectedError(it) }
        }
    }

    CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler + Job()).launch {
        withContext(Dispatchers.Main) {
            if (progress) {
                progressBarAlert(context)
            }
        }

        try {
            // Prepare request body
            val gson = Gson()
            val requestBody = request?.let {
                val jsonString = gson.toJson(it)
                val encryptedRequest =
                    encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString) ?: jsonString
                encryptedRequest.toRequestBody("text/plain".toMediaTypeOrNull())
            }

            // Make the API call
            val response: Response<ResponseBody> = requestBody?.let { apiCall(it) } ?: return@launch

            // Check the HTTP status code
            if (response.code() == 429) {
                // Handle rate limit exceeded
                val retryAfter = response.headers()["Retry-After"]?.toLongOrNull() ?: 2L
                withContext(Dispatchers.Main) {
                    onUnexpectedError("Rate limit exceeded. Retry after $retryAfter seconds.")
                }
                return@launch // Early exit
            } else if (
                response.code() == 498
            ) {
                val plainTextResponse = response.errorBody()?.string()
                val decryptedResponse =
                    decryptKey(BuildConfig.SECRET_KEY_UAT, plainTextResponse)
                val parsedResponse: BaseResponse<T> =
                    gson.fromJson(
                        decryptedResponse,
                        object : TypeToken<BaseResponse<T>>() {}.type
                    )
                onTokenExpired(parsedResponse)
                return@launch

            }

            // Handle other non-200 status codes
            if (response.isSuccessful) {
                // Successful response
                val plainTextResponse = response.body()?.string()
                val decryptedResponse =
                    decryptKey(BuildConfig.SECRET_KEY_UAT, plainTextResponse)

                // Try to parse the response
                val parsedResponse: BaseResponse<T>? = try {
                    gson.fromJson(
                        decryptedResponse,
                        object : TypeToken<BaseResponse<T>>() {}.type
                    )
                } catch (e: JsonSyntaxException) {
                    null // If parsing fails, return null
                }

                withContext(Dispatchers.Main) {
                    if (parsedResponse != null) {
                        when (parsedResponse.status_code) {
                            200,201-> {
                                hideProgressBar()
                                onSuccess(parsedResponse)
                            }

                            400, 401, 429 -> {
                                hideProgressBar()
                                onClientError(
                                    parsedResponse.status_code,
                                    parsedResponse.message ?: "Client error"
                                )
                            }

                            498 -> {
                                hideProgressBar()
                                onTokenExpired(parsedResponse)
                            }

                            else -> {
                                hideProgressBar()
                                parsedResponse?.status_code?.let {
                                    onClientError(
                                        it,
                                        parsedResponse.message ?: "Unexpected error occurred"
                                    )
                                }
                            }
                        }
                    } else {
                        hideProgressBar()
                        onUnexpectedError("Error body: $decryptedResponse")
                    }
                }
            } else {
                // Handle failure cases
                hideProgressBar()


                val plainTextResponse = response.errorBody()?.string()
                val decryptedResponse =
                    decryptKey(BuildConfig.SECRET_KEY_UAT, plainTextResponse)
                val parsedResponse: BaseResponse<T> =
                    gson.fromJson(
                        decryptedResponse,
                        object : TypeToken<BaseResponse<T>>() {}.type
                    )
                parsedResponse.status_code?.let { onClientError(it,parsedResponse.message.toString()) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                hideProgressBar()
                onUnexpectedError(e.localizedMessage ?: "Exception occurred")
            }
        } finally {
            withContext(Dispatchers.Main) {
                hideProgressBar()
            }
        }
    }
}
