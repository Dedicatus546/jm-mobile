package com.par9uet.jm.retrofit

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class LoginCookieJar : CookieJar {
    private var cookieStore: List<Cookie> = listOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (url.encodedPath.contains("login")) {
            // 只拦截登录的 cookie
            cookieStore = cookies
            Log.d("save cookie", "${url.encodedPath} - $cookieStore")
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        Log.d("load cookie", cookieStore.toString())
        return cookieStore
    }
}