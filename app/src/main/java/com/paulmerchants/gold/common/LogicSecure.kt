package com.paulmerchants.gold.common

interface LogicSecure {
    fun getSHA256(key: String): String
    fun encryptKey(strToEncrypt: String, secret_key: String): String?
    fun decryptKey(key: String, strToDecrypt: String?): String?
}

