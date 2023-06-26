package com.paulmerchants.gold.networks

import com.paulmerchants.gold.remote.ApiParams

/**
 * Call Handler
 * There is a three methods
 * send request , success , error
 * */
interface CallHandler<T> {

    /**
     * This Method for call a send
     * request with api interface
     * */
    suspend fun sendRequest(apiParams: ApiParams): T

    /**
     * This method is for get
     * a success response
     * */
    fun success(response: T)

    /**
     * This method is for
     * get a error messages
     */
    fun error(message: String) {
        //No Need TO Import That
    }

}