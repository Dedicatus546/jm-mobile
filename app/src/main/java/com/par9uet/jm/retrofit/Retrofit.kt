package com.par9uet.jm.retrofit

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.par9uet.jm.retrofit.converter.PrimitiveToRequestBodyConverterFactory
import com.par9uet.jm.retrofit.converter.ResponseConverterFactory
import com.par9uet.jm.retrofit.interceptor.TokenInterceptor
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

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