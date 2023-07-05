package com.paulmerchants.gold.security

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.common.LogicSecure
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
import java.util.Locale
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.ShortBufferException
import javax.crypto.spec.SecretKeySpec


class SecureFiles(val context: Context) : LogicSecure {

    //    SECRETKEY: MobApp#L0AnDeT@ils#SeCUretechpool*20&23
//    val secretKey = "MobApp#L0AnDeT@ils#SeCUretechpool*20&23"
    val secret = BuildConfig.SECRET_KEY
    override fun getSHA256(key: String): String {
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        val messageDigest = md.digest(key.toByteArray())
        // Convert byte array into signum representation
        val no = BigInteger(1, messageDigest)
        // Convert message digest into hex value
        var hashText: String = no.toString(16)
        // Add preceding 0s to make it 128 chars long
        while (hashText.length < 256) {
            hashText = "0$hashText"
        }
        return hashText.substring(0, 32)
            .uppercase(Locale.getDefault())
    }

    override fun encryptKey(strToEncrypt: String, secret_key: String): String? {
        Security.addProvider(BouncyCastleProvider())
        val keyBytes: ByteArray

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

    override fun decryptKey(key: String, strToDecrypt: String?): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = key.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = org.bouncycastle.util.encoders.Base64
                .decode(strToDecrypt?.trim { it <= ' ' }?.toByteArray(charset("UTF8")))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.DECRYPT_MODE, skey)

                val plainText = ByteArray(cipher.getOutputSize(input.size))
                var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim { it <= ' ' }
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


    //This is the app's internal storage folder
    //val baseDir = getApplication(context).filesDir
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
