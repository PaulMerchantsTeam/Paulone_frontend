import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.responsemodels.BaseResponse
import com.paulmerchants.gold.utility.AppUtility.hideProgressBar
import com.paulmerchants.gold.utility.AppUtility.progressBarAlert
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
import com.paulmerchants.gold.utility.parseJson
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
    crossinline apiCall: suspend (RequestBody) -> Response<ResponseBody>,
    crossinline onSuccess: (BaseResponse<T>) -> Unit,
    crossinline onTokenExpired: (BaseResponse<T>) -> Unit,
    crossinline onClientError: (BaseResponse<T>) -> Unit,
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
            if (progress) progressBarAlert(context)
        }

        try {
            val gson = Gson()
            val requestBody = request?.let {
                val jsonString = gson.toJson(it)
                val encryptedRequest =
                    encryptKey(BuildConfig.SECRET_KEY_UAT, jsonString) ?: jsonString
                encryptedRequest.toRequestBody("text/plain".toMediaTypeOrNull())
            }

            val response: Response<ResponseBody> = requestBody?.let { apiCall(it) } ?: return@launch

                when (response.code()) {
                    200, 201 -> {
                        val plainTextResponse = response.body()?.string()
                        val decryptedResponse = decryptKey(BuildConfig.SECRET_KEY_UAT, plainTextResponse)

                        Log.d("TAG", "Raw Response: $plainTextResponse")
                        Log.d("TAG", "Decrypted Response: $decryptedResponse")

                        if (decryptedResponse.isNullOrEmpty()) {
                            onUnexpectedError("Decrypted response is empty.")
                            return@launch
                        }

                        val parsedResponse: BaseResponse<T>? = parseJson(decryptedResponse)
                        when (parsedResponse?.status_code) {
                            200, 201 -> onSuccess(parsedResponse)
                            498 -> onTokenExpired(parsedResponse)
                            else -> parsedResponse?.let(onClientError)
                        }
                    }
                    422->{
                        val errorResponse = response.errorBody()?.string()
                        val decryptedError = decryptKey(BuildConfig.SECRET_KEY_UAT, errorResponse)
                        val parsedErrorResponse: BaseResponse<T>? = parseJson(decryptedError)
                        parsedErrorResponse?.let(onClientError)
                        return@launch
                    }

                    429 -> {
                        // Handle rate limit exceeded
                        val retryAfter = response.headers()["Retry-After"]?.toLongOrNull() ?: 2L
                        withContext(Dispatchers.Main) {
                            onUnexpectedError("Rate limit exceeded. Retry after $retryAfter seconds.")
                        }
                        return@launch // Early exit
                    }
                    498 -> {
                        val plainTextErrorResponse = response.errorBody()?.string()
                        val decryptedErrorResponse =
                            decryptKey(BuildConfig.SECRET_KEY_UAT, plainTextErrorResponse)
                        val parsedErrorResponse: BaseResponse<T>? =
                            parseJson(decryptedErrorResponse)
                        parsedErrorResponse?.let(onTokenExpired)
                        return@launch
                    }
                    else -> {
                        val errorResponse = response.errorBody()?.string()
                        val decryptedError = decryptKey(BuildConfig.SECRET_KEY_UAT, errorResponse)
                        val parsedErrorResponse: BaseResponse<T>? = parseJson(decryptedError)
                        parsedErrorResponse?.let(onClientError)
                            ?: onUnexpectedError("Error parsing response.")
                    }
                }


        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onUnexpectedError(e.localizedMessage ?: "Exception occurred.")
            }
        } finally {
            withContext(Dispatchers.Main) {
                hideProgressBar()
            }
        }
    }
}
