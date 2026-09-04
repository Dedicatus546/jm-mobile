package com.par9uet.jm.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.par9uet.jm.utils.decrypt
import com.par9uet.jm.utils.encrypt
import jakarta.inject.Inject
import com.par9uet.jm.utils.json
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Singleton

@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("jm-mobile-g-data", Context.MODE_PRIVATE)

    inline fun <reified T> set(key: String, value: T) {
        val jsonStr = json.encodeToString(value)
        sharedPreferences.edit {
            putString(key, encrypt(jsonStr))
        }
    }

    inline fun <reified T> get(key: String): T? {
        val jsonStr = sharedPreferences.getString(key, null)
        return try {
            jsonStr?.let {
                json.decodeFromString(decrypt(it))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun remove(key: String) {
        sharedPreferences.edit {
            remove(key)
        }
    }
}