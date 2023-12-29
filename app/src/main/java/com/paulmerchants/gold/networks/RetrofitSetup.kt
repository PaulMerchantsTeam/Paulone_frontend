package com.paulmerchants.gold.networks


import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import com.paulmerchants.gold.databinding.ProgressLayoutBinding
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.ui.MainActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class RetrofitSetup @Inject constructor(private val apiParams: ApiParams) {

    fun <T> callApi(
        progress: Boolean,
        callHandler: CallHandler<T>,
    ) {

        /**
         * Coroutine Exception Handler
         * */
        val coRoutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            CoroutineScope(Dispatchers.Main).launch {
                throwable.message.let { callHandler.error(it ?: "") }
            }
        }


        /**
         * Call Api
         * */
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler + Job()).launch {
            flow {
                withContext(Dispatchers.Main) {
                    if (progress) progressBarAlert()
                }
                emit(callHandler.sendRequest(apiParams = apiParams) as Response<*>)
            }.flowOn(Dispatchers.IO).catch { error ->
                Log.d("Response", "callApi: ${error.message}")
                withContext(Dispatchers.Main) {
                    hideProgressBar()
                    if (error.localizedMessage?.contains("Failed to connect", true) == true) {
//                        error.localizedMessage.showSnackBar()
                    } else callHandler.error(error.localizedMessage ?: "Something Went Wrong.")
                }
            }.collect { response ->
                withContext(Dispatchers.Main) {
                    hideProgressBar()
                    Log.d("Response", "callApi: ${response.isSuccessful}")
                    if (response.isSuccessful) { //200...300
                        callHandler.success(response as T)
                    } else {
                        Log.d("RETROFITSETUP", "callApi: ${response.code()}")
                        when (response.code()) {
                            400 -> {
                                //400 Bad Request
//                                response.message().showSnackBar()
                            }

                            401 -> {
                                //token expired case
                                callHandler.success(response as T)
//                                response.message().showSnackBar()
                            }


                            404 -> {
                                callHandler.success(response as T)
                            }

                            500 -> {
                                callHandler.success(response as T)

                            }

                            403 -> {
                                callHandler.success(response as T)
                            }


                            else -> response.errorBody()?.string().let {
                                Log.d("TAG", "callApi: ........$it")
                                try {

                                    callHandler.error(JSONObject(it).getString("responseMessage"))

                                } catch (e: Exception) {
                                    Log.d("ErrorParsing", "callApi:${e.message} ")
                                }

                            }
                        }

                    }
                }
            }
        }

    }


    /**
     * Get Response error
     * */
    private fun getResponseError(string: String): String {
        return try {
            val jsonObject = JSONObject(string)
            if (jsonObject.has("message")) {
                jsonObject.getString("message")
            } else {
                "Something Went Wrong."
            }
        } catch (e: JSONException) {
            string
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


    /**
     * Progress Bar Layout
     * */
    private lateinit var dialog: AlertDialog

    private fun progressBarAlert() = try {
        hideProgressBar()
        MainActivity.context.get()?.let {
            val builder = AlertDialog.Builder(it)
            val layout = ProgressLayoutBinding.inflate(LayoutInflater.from(it))
            builder.setCancelable(false)
            builder.setView(layout.root)
            dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }


    /** Hide Progress Bar */
    private fun hideProgressBar() {
        try {
            if (::dialog.isInitialized) dialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}