package com.paulmerchants.gold.sharedpref

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.paulmerchants.gold.common.Constants.SHARED_PREF_FILE

object AppSharedPref {
    private val mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private lateinit var preferences: SharedPreferences

    fun start(context: Context) {
        preferences = EncryptedSharedPreferences.create(
            SHARED_PREF_FILE,
            mainKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBooleanValue(key: String): Boolean {
        return preferences.getBoolean(key, false)
    }

    fun putStringValue(key: String, value: String) {
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringValue(key: String): String? {
        return preferences.getString(key, "")
    }

    fun putIntValue(key: String, value: Int) {
        val editor = preferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getIntValue(key: String?): Int {
        return preferences.getInt(key, 0)
    }

    fun putLongValue(key: String, value: Long) {
        val editor = preferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLongValue(key: String): Long {
        return preferences.getLong(key, 0L)
    }

    fun putMap(key: String) {

    }


    fun clearSharedPref() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun removeSingleValueFromSharedPref(key: String) {
        preferences.edit().remove(key).apply()
    }

}