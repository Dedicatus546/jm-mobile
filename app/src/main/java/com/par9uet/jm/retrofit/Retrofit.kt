package com.par9uet.jm.retrofit

import com.par9uet.jm.retrofit.converter.PrimitiveToRequestBodyConverterFactory
import com.par9uet.jm.retrofit.converter.ResponseConverterFactory
import com.par9uet.jm.retrofit.interceptor.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class Retrofit(
    private val tokenInterceptor: TokenInterceptor,
    private val loginCookieJar: LoginCookieJar,
    private val responseConverterFactory: ResponseConverterFactory,
    private val primitiveToRequestBodyConverterFactory: PrimitiveToRequestBodyConverterFactory
) {
    private var apiList = listOf("https://www.cdnzack.cc")
    private var baseUrl = apiList[0]
    private val okHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .cookieJar(loginCookieJar)
            .build()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(responseConverterFactory)
            .addConverterFactory(primitiveToRequestBodyConverterFactory)
            .build()
    }

    fun <T> createService(cls: Class<T>): T {
        val service = retrofit.create(cls)
        return service
    }
}