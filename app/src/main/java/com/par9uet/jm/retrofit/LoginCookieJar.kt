package com.par9uet.jm.retrofit

import com.google.gson.reflect.TypeToken
import com.par9uet.jm.storage.SecureStorage
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class LoginCookieJar(
    private val secureStorage: SecureStorage
) : CookieJar {
    private var cookieStore: List<Cookie> =
        secureStorage.get("cookie", object : TypeToken<List<Cookie>>() {}.type) ?: listOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore =
            (cookieStore + cookies).associateBy { "${it.domain}:${it.path}:${it.name}" }.values.toList()
        secureStorage.save("cookie", cookieStore)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore
    }

    fun clearCookie() {
        cookieStore = listOf()
        secureStorage.save("cookie", cookieStore)
    }
}