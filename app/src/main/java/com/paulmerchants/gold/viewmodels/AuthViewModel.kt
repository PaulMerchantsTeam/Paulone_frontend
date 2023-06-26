package com.paulmerchants.gold.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.SecureFiles
import com.shacklabs.quicke.remote.networks.RetrofitSetup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val retrofitSetup: RetrofitSetup) : ViewModel() {
    val pass = "FU510N@pro"
    val userId = "pml"

    fun getLogin(secureFiles: SecureFiles) = viewModelScope.launch {
        Log.d("TAG", "getLogin: //../........")
        retrofitSetup.callApi(true, object : CallHandler<Response<RespLogin>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<RespLogin> {
                return apiParams.getLogin(
                    RequestLogin(
                        "b7ah0vVuRsjzEfx/R6CHAg==",
                        "+rxS+i2nKgwhZi0QXKoKfw=="
                    )
                )
            }

            override fun success(response: Response<RespLogin>) {
                Log.d("TAG", "success: ......${response.isSuccessful}")
                Log.d("TAG", "success: ......${response.body()}")
            }

            override fun error(message: String) {
                super.error(message)
                Log.d("TAG", "error: ......$message")
            }
        })
    }

}