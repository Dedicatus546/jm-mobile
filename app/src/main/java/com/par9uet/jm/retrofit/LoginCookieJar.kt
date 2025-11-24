package com.par9uet.jm.retrofit

import android.util.Log
import com.par9uet.jm.storage.SecureStorage
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class LoginCookieJar(
    private val secureStorage: SecureStorage
) : CookieJar {
    private var cookieStore: List<Cookie> = secureStorage.getLoginCookies() ?: listOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (url.encodedPath.contains("login")) {
            // 只拦截登录的 cookie
            cookieStore = cookies
            secureStorage.saveLoginCookies(cookieStore)
            Log.d("save cookie", "${url.encodedPath} - $cookieStore")
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        Log.d("load cookie", cookieStore.toString())
        return cookieStore
    }
}