package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.RequestLogin
import com.paulmerchants.gold.model.RespLogin
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.CUSTOMER_ID
import com.paulmerchants.gold.utility.Constants.CUSTOMER_NAME
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import com.shacklabs.quicke.remote.networks.RetrofitSetup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val apiParams: ApiParams) : ViewModel() {
    //    val pass = "FU510N@pro"
//    val userId = "pml"
    var isStartAnim = MutableLiveData<Boolean>()
    var isCustomerExist = MutableLiveData<Boolean>()

    fun getCustomer(mobileNum: String, context: Context) = viewModelScope.launch {
        try {
            val encMobile =
                SecureFiles(context).encryptKey(mobileNum, BuildConfig.SECRET_KEY_GEN).toString()

            AppSharedPref.getStringValue(JWT_TOKEN)?.let {
                val response = apiParams.getCustomer(it, encMobile)
                val plainTextResponse = response.string()
                Log.d("Response", plainTextResponse)

                val decryptData = decryptKey(
                    BuildConfig.SECRET_KEY_GEN,
                    plainTextResponse
                )
                println("decrypt-----$decryptData")

                val respLogin = AppUtility.stringToJsonCustomer(decryptData.toString())
                //getting initially first customer
                if (respLogin[0].Status == true) {
                    isCustomerExist.postValue(true)
                    AppSharedPref.putStringValue(CUSTOMER_ID, respLogin[0].CustID.toString())
                    AppSharedPref.putStringValue(
                        CUSTOMER_NAME, respLogin[0].CustName.toString()
                    )
                } else {
                    Toast.makeText(
                        context, "Error: Status = ${respLogin[0].Status}", Toast.LENGTH_SHORT
                    ).show()
                }
                AppUtility.hideProgressBar()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("TAG", "getCustomer: ........${e.message}")
        }
    }

}