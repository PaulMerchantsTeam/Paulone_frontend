package com.paulmerchants.gold.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.RespGetCustomer
import com.paulmerchants.gold.model.newmodel.LoginNewResp
import com.paulmerchants.gold.model.newmodel.LoginReqNew
import com.paulmerchants.gold.model.newmodel.ReqCustomerNew
import com.paulmerchants.gold.model.newmodel.RespCutomerInfo
import com.paulmerchants.gold.networks.CallHandler
import com.paulmerchants.gold.networks.RetrofitSetup
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.Constants.CUSTOMER_ID
import com.paulmerchants.gold.utility.Constants.CUSTOMER_NAME
import com.paulmerchants.gold.utility.Constants.JWT_TOKEN
import com.paulmerchants.gold.utility.decryptKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiParams: ApiParams,
    private val retrofitSetup: RetrofitSetup,
) : ViewModel() {
    //    val pass = "FU510N@pro"
//    val userId = "pml"
    var isStartAnim = MutableLiveData<Boolean>()
    var isCustomerExist = MutableLiveData<Boolean>()

    fun getCustomer(mobileNum: String, context: Context) = viewModelScope.launch {

//        if (mobileNum == "9999988888") {
//            isCustomerExist.postValue(true)
//            return@launch
//        }

        retrofitSetup.callApi(true, object : CallHandler<Response<RespCutomerInfo>> {
            override suspend fun sendRequest(apiParams: ApiParams): Response<RespCutomerInfo> {
                return apiParams.getCustomer(
                    "Bearer ${AppSharedPref.getStringValue(JWT_TOKEN).toString()}",
                    ReqCustomerNew(mobileNum, AppUtility.getDeviceDetails())
                )
            }

            override fun success(response: Response<RespCutomerInfo>) {
                try {
                    val decryptData = decryptKey(
                        BuildConfig.SECRET_KEY_GEN, response.body()?.data
                    )
                    val respCutomer: RespGetCustomer? =
                        AppUtility.convertStringToJson(decryptData.toString())
                    //getting initially first customer
                    if (respCutomer != null) {
                        if (respCutomer[0].Status == true) {
                            isCustomerExist.postValue(true)
                            Log.d(
                                "TAG",
                                "getCustomer: -CustomerId---${respCutomer[0].Cust_ID.toString()}"
                            )
                            Log.d(
                                "TAG",
                                "getCustomer: -CustName---${respCutomer[0].CustName.toString()}"
                            )
                            AppSharedPref.putStringValue(
                                CUSTOMER_ID,
                                respCutomer[0].Cust_ID.toString()
                            )
                            AppSharedPref.putStringValue(
                                CUSTOMER_NAME, respCutomer[0].CustName.toString()
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Error: Status = ${respCutomer[0].Status}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.d("TAG", "success: ........${e.message}")
                }
            }


            override fun error(message: String) {
                super.error(message)
                Log.d("TAG", "error: ......$message")
            }
        })
    }
    /*
        fun getCustomer(mobileNum: String, context: Context) = viewModelScope.launch {

            if (mobileNum == "9999988888") {
                isCustomerExist.postValue(true)
                return@launch
            }

            try {
                val encMobile =
                    SecureFiles().encryptKey(mobileNum, BuildConfig.SECRET_KEY_GEN).toString()

                AppSharedPref.getStringValue(JWT_TOKEN)?.let {
                    val response = apiParams.getCustomer(it, encMobile)
                    val plainTextResponse = response.string()
                    Log.d("Response", plainTextResponse)

                    val decryptData = decryptKey(
                        BuildConfig.SECRET_KEY_GEN, plainTextResponse
                    )
                    println("decrypt-----$decryptData")

                    val respCutomer: RespGetCustomer? =
                        AppUtility.convertStringToJson(decryptData.toString())
    //                val respCutomer = AppUtility.stringToJsonCustomer(decryptData.toString())
                        //getting initially first customer
                        if (respCutomer != null) {
                            if (respCutomer[0].Status == true) {
                                isCustomerExist.postValue(true)
                                Log.d(
                                    "TAG", "getCustomer: -CustomerId---${respCutomer[0].Cust_ID.toString()}"
                                )
                                Log.d(
                                    "TAG", "getCustomer: -CustName---${respCutomer[0].CustName.toString()}"
                                )
                                AppSharedPref.putStringValue(CUSTOMER_ID, respCutomer[0].Cust_ID.toString())
                                AppSharedPref.putStringValue(
                                    CUSTOMER_NAME, respCutomer[0].CustName.toString()
                                )
                            } else {
                                Toast.makeText(
                                    context, "Error: Status = ${respCutomer[0].Status}", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    AppUtility.hideProgressBar()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TAG", "getCustomer: ........${e.message}")
            }
        }
    */

}