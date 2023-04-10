package com.paulmerchants.gold.security

import android.app.Application
import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.internal.Contexts.getApplication
import java.io.File
import java.nio.charset.StandardCharsets

class SecureFiles(val context: Context) {

    //This is the app's internal storage folder
//    val baseDir = getApplication(context).filesDir
    private val mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    lateinit var encryptedFile: EncryptedFile
    fun start() {
        encryptedFile = EncryptedFile.Builder(
            File(getApplication(context.applicationContext).filesDir, "encrypted-file.txt"),
            context,
            mainKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }
    // Although you can define your own key generation parameter specification, it's
    // recommended that you use the value specified here.


    //The encrypted file within the app's internal storage folder

    //This is the app's internal storage folder


    //The encrypted file within the app's internal storage folder

    //Create the encrypted file


    fun writeEncryptedContent(content: String) {
        //Open the file for writing, and write our contents to it.
        //Note how Kotlin's 'use' function correctly closes the resource after we've finished,
        //regardless of whether or not an exception was thrown.
        encryptedFile.openFileOutput().use {
            it.write(content.toByteArray(StandardCharsets.UTF_8))
            it.flush()
        }
    }

    /**
     * Beware that the above code only reads the first 32KB of the file
     */

    fun readFromEncryptedFile(): String {
        //We will read up to the first 32KB from this file. If your file may be larger, then you
        //can increase this value, or read it in chunks.
        val fileContent = ByteArray(32000)

        //The number of bytes actually read from the file.
        val numBytesRead: Int

        //Open the file for reading, and read all the contents.
        //Note how Kotlin's 'use' function correctly closes the resource after we've finished,
        //regardless of whether or not an exception was thrown.
        encryptedFile.openFileInput().use {
            numBytesRead = it.read(fileContent)
        }

        return String(fileContent, 0, numBytesRead)
    }

}