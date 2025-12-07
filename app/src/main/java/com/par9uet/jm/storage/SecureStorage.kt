package com.par9uet.jm.storage

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.par9uet.jm.data.models.LocalSetting
import com.par9uet.jm.data.models.AutoLogin
import com.par9uet.jm.data.models.User
import okhttp3.Cookie
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureStorage(
    context: Context,
    private val gson: Gson = GsonBuilder().create()
) {
    private val cryptoManager = CryptoManager()
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("jm-mobile-g-data", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        val json = gson.toJson(user)
        sharedPreferences.edit {
            putString("user", cryptoManager.encrypt(json))
        }
    }

    fun getUser(): User? {
        val json = sharedPreferences.getString("user", null)
        return try {
            json?.let {
                gson.fromJson(cryptoManager.decrypt(it), User::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveLoginCookies(cookies: List<Cookie>) {
        val json = gson.toJson(cookies)
        sharedPreferences.edit {
            putString("loginCookies", cryptoManager.encrypt(json))
        }
    }

    fun getLoginCookies(): List<Cookie>? {
        val json = sharedPreferences.getString("loginCookies", null)
        return try {
            json?.let {
                val type = object : TypeToken<List<Cookie>>() {}.type
                gson.fromJson(cryptoManager.decrypt(it), type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveLocalSetting(localSetting: LocalSetting) {
        val json = gson.toJson(localSetting)
        sharedPreferences.edit {
            putString("localSetting", cryptoManager.encrypt(json))
        }
    }

    fun getLocalSetting(): LocalSetting? {
        val json = sharedPreferences.getString("localSetting", null)
        return try {
            json?.let {
                gson.fromJson(cryptoManager.decrypt(it), LocalSetting::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveAutoLogin(autoLogin: AutoLogin) {
        val json = gson.toJson(autoLogin)
        sharedPreferences.edit {
            putString("autoLogin", cryptoManager.encrypt(json))
        }
    }

    fun getAutoLogin(): AutoLogin? {
        val json = sharedPreferences.getString("autoLogin", null)
        return try {
            json?.let {
                gson.fromJson(cryptoManager.decrypt(it), AutoLogin::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

class CryptoManager {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val keyAlias = "app_master_key"

    private fun getSecretKey(): SecretKey {
        // 检查是否已存在密钥
        val existingKey = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
        if (existingKey != null) {
            return existingKey.secretKey
        }

        // 创建新密钥
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val keySpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setKeySize(256)
            setUserAuthenticationRequired(false)
        }.build()

        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        val encrypted = cipher.doFinal(data.toByteArray())
        val iv = cipher.iv

        // 组合加密数据和 IV
        val result = encrypted + iv
        return Base64.encodeToString(result, Base64.NO_WRAP)
    }

    fun decrypt(encryptedData: String): String? {
        return try {
            val data = Base64.decode(encryptedData, Base64.NO_WRAP)

            // GCM IV 通常是 12 字节
            val iv = data.copyOfRange(data.size - 12, data.size)
            val encryptedBytes = data.copyOfRange(0, data.size - 12)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

            String(cipher.doFinal(encryptedBytes))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}