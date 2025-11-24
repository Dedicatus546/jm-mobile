package com.par9uet.jm.retrofit.interceptor

import com.par9uet.jm.retrofit.API_TOKEN_HASH
import com.par9uet.jm.retrofit.API_TS
import com.par9uet.jm.retrofit.API_VERSION
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val newRequest = originalRequest.newBuilder()
            .addHeader("tokenparam", "${API_TS},${API_VERSION}")
            .addHeader("token", API_TOKEN_HASH)
            .build()
        return chain.proceed(newRequest)
    }
}