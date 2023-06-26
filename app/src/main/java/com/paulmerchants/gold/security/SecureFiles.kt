package com.paulmerchants.gold.security

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.internal.Contexts.getApplication
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.io.File
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.ShortBufferException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class SecureFiles(val context: Context) {

    fun encryptStr(strToEncrypt: String): ByteArray {
        val plainText = strToEncrypt.toByteArray(Charsets.UTF_8)
        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(256)
        val key = keygen.generateKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val cipherText = cipher.doFinal(plainText)
        return cipherText
    }

    fun getSHA256(hashedInput: String): String {
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        val messageDigest = md.digest(hashedInput.toByteArray())
        // Convert byte array into signum representation
        val no = BigInteger(1, messageDigest)
        // Convert message digest into hex value
        var hashText: String = no.toString(16)
        // Add preceding 0s to make it 128 chars long
        while (hashText.length < 256) {
            hashText = "0$hashText"
        }
        return hashText
    }


    //This is the app's internal storage folder
//    val baseDir = getApplication(context).filesDir
    val mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
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

fun generateSecretKey(): SecretKey {
    val keyGenerator = KeyGenerator.getInstance("AES")
    keyGenerator.init(256) // Specify the key size (in bits), in this case, 256 bits for AES-256
    return keyGenerator.generateKey()
}

fun getSHA256(hashedInput: String): String {
    val md: MessageDigest = MessageDigest.getInstance("SHA-256")
    val messageDigest = md.digest(hashedInput.toByteArray())
    // Convert byte array into signum representation
    val no = BigInteger(1, messageDigest)
    // Convert message digest into hex value
    var hashText: String = no.toString(16)
    // Add preceding 0s to make it 128 chars long
//    while (hashText.length < 128) {
//        hashText = "0$hashText"
//    }
    return hashText
}

fun encryptStr(strToEncrypt: String): ByteArray {
    val plainText = strToEncrypt.toByteArray(Charsets.UTF_8)
    val keygen = KeyGenerator.getInstance("AES")
    keygen.init(256)
    val key = keygen.generateKey()
    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    val cipherText = cipher.doFinal(plainText)
    return cipherText
}


fun encrypt(strToEncrypt: String, secret_key: String): String? {
    Security.addProvider(BouncyCastleProvider())
    var keyBytes: ByteArray

    try {
        keyBytes = secret_key.toByteArray(charset("UTF8"))
        val skey = SecretKeySpec(keyBytes, "AES")
        val input = strToEncrypt.toByteArray(charset("UTF8"))

        synchronized(Cipher::class.java) {
            val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, skey)

            val cipherText = ByteArray(cipher.getOutputSize(input.size))
            var ctLength = cipher.update(
                input, 0, input.size,
                cipherText, 0
            )
            ctLength += cipher.doFinal(cipherText, ctLength)
            return String(
                Base64.encode(cipherText)
            )
        }
    } catch (uee: UnsupportedEncodingException) {
        uee.printStackTrace()
    } catch (ibse: IllegalBlockSizeException) {
        ibse.printStackTrace()
    } catch (bpe: BadPaddingException) {
        bpe.printStackTrace()
    } catch (ike: InvalidKeyException) {
        ike.printStackTrace()
    } catch (nspe: NoSuchPaddingException) {
        nspe.printStackTrace()
    } catch (nsae: NoSuchAlgorithmException) {
        nsae.printStackTrace()
    } catch (e: ShortBufferException) {
        e.printStackTrace()
    }

    return null
}

@RequiresApi(Build.VERSION_CODES.O)
fun secretKeyToString(secretKey: SecretKey): String {
    return java.util.Base64.getEncoder().encodeToString(secretKey.encoded)
}

@RequiresApi(Build.VERSION_CODES.O)
fun encryptStrings(input: String, secretKey: SecretKey): ByteArray {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val iv = ByteArray(cipher.blockSize)
    val ivSpec = IvParameterSpec(iv)

    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
    val encryptedBytes = cipher.doFinal(input.toByteArray())
    return java.util.Base64.getEncoder().encodeToString(encryptedBytes).toByteArray()
}

fun decrypt(cipherText: kotlin.ByteArray?, key: javax.crypto.SecretKey, IV: kotlin.ByteArray?): kotlin.String?{
    try {
        val cipher: Cipher = Cipher.getInstance("AES")
        val keySpec = SecretKeySpec(key.getEncoded(), "AES")
        val ivSpec = IvParameterSpec(IV)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decryptedText: kotlin.ByteArray = cipher.doFinal(cipherText)
        return String(decryptedText)
    }catch ( e: java.lang.Exception){
        e.printStackTrace()
    }
    return null
}


@RequiresApi(Build.VERSION_CODES.O)
fun main() {
//    val a = getSHA256("vishnuap")
//    println(a)
//    println(encryptStr("vishnuap"))
//    println(encryptString("vishnuap",generateSecretKey()))
    val secretKey = generateSecretKey()
//    println(encrypt("vishnuap", secretKeyToString(secretKey)))
    println(encryptStrings("pml", secretKey))
    println(encryptStrings("FU510N@pro", secretKey))
    println(encryptStr("FU510N@pro"))
    println(encryptStr("pml"))
}