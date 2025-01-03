package com.paulmerchants.gold.utility

import com.paulmerchants.gold.BuildConfig

object Constants {
    const val AUTH_STATUS = "AUTH_STATUS"
    const val JWT_TOKEN = "JWT_TOKEN"
    const val CUSTOMER_ID = "CUSTOMER_ID"
    const val SESSION_ID = "SESSION_ID"
    const val REFRESH_TOKEN = "REFRESH_TOKEN"
    const val ACC_NUM = "ACC_NUM"
    const val CUSTOMER_NAME = "CUSTOMER_NAME"
    const val CUST_MOBILE = "CUST_MOBILE"
    const val CUST_EMAIL = "CUST_EMAIL"
    const val CUSTOMER_FULL_DATA = "CUSTOMER_FULL_DATA"
    const val IS_LOGOUT = "IS_LOGOUT"
    const val IS_RESET_MPIN = "IS_RESET_MPIN"
    const val PAYMENT_ID = "PAYMENT_ID"
    const val ORDER_ID = "ORDER_ID"
    const val GO_TO_HOME = "GO_TO_HOME"
    const val IS_RESET_MPIN_FROM_LOGIN_PAGE = "IS_RESET_MPIN_FROM_LOGIN_PAGE"
    const val DUE_LOAN_DATA = "DUE_LOAN_DATA"
    const val PAY_ALL_IN_GO_DATA = "PAY_ALL_IN_GO_DATA"
    const val LOAN_OVERVIEW = "LOAN_OVERVIEW"
    const val BBPS_TYPE = "BBPS_TYPE"
    const val BBPS_HEADER = "BBPS_HEADER"

    const val LOGIN_WITH_MPIN = "LOGIN_WITH_MPIN"
    const val PHONE_LOGIN = "PHONE_LOGIN"

    const val SHARED_PREF_FILE = "${BuildConfig.VERSION_NAME}_${BuildConfig.VERSION_CODE}_app"


    //Page_visit_boolean
    const val SPLASH_SCRN_VISITED = "SPLASH_SCRN_VISITED"
    const val SIGNUP_DONE = "SIGNUP_DONE"
    const val IS_USER_EXIST = "IS_USER_EXIST"
    const val OTP_VERIFIED = "OTP_VERIFIED"
    const val AMOUNT_PAYABLE = "AMOUNT_PAYABLE"
    const val CUST_ACC = "CUST_ACC"
    const val IS_COME_GOLD = "IS_COME_GOLD"
    const val IS_CUSTOM_AMOUNT = "IS_CUSTOM_AMOUNT"

}