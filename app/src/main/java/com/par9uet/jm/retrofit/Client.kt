package com.par9uet.jm.retrofit

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.par9uet.jm.storage.SecureStorage
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
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
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

private fun md5(str: String): String {
    return MessageDigest.getInstance("MD5").digest(str.toByteArray())
        .joinToString("") { "%02x".format(it) }.lowercase()
}

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

object RetrofitClient {
    private var apiList = listOf("https://www.jmapiproxyxxx.vip")
    private var BASE_URL = apiList[0]
    private val ts = System.currentTimeMillis() / 1000
    private const val version = "1.8.2"
    private const val tokenSecret = "185Hcomic3PAPP7R"
    private val tokenHash = md5("${ts}${tokenSecret}")
    private val loginCookieJar = LoginCookieJar()
    private val primitiveToRequestBodyConverterFactory = object : Converter.Factory() {
        override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<*, RequestBody>? {
            return when (type) {
                Int::class.java -> Converter<Int, RequestBody> { value ->
                    value.toString().toRequestBody("text/plain".toMediaType())
                }

                Long::class.java -> Converter<Long, RequestBody> { value ->
                    value.toString().toRequestBody("text/plain".toMediaType())
                }

                Float::class.java -> Converter<Float, RequestBody> { value ->
                    value.toString().toRequestBody("text/plain".toMediaType())
                }

                Double::class.java -> Converter<Double, RequestBody> { value ->
                    value.toString().toRequestBody("text/plain".toMediaType())
                }

                Boolean::class.java -> Converter<Boolean, RequestBody> { value ->
                    value.toString().toRequestBody("text/plain".toMediaType())
                }

                String::class.java -> Converter<String, RequestBody> { value ->
                    value.toRequestBody("text/plain".toMediaType())
                }

                else -> null
            }
        }
    }
    private val tokenInterceptor = Interceptor { chain ->
        val originalRequest: Request = chain.request()
        val newRequest = originalRequest.newBuilder()
            .addHeader("tokenparam", "${ts},${version}")
            .addHeader("token", tokenHash)
            .build()
        chain.proceed(newRequest)
    }
    private val responseConverterFactory = object : Converter.Factory() {
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *> {
            val gsonConverter =
                GsonConverterFactory.create(Gson())
                    .responseBodyConverter(type, annotations, retrofit)
            return Converter<ResponseBody, Any> { responseBody ->
                val body = responseBody.string()
                val json = Gson().fromJson(body, JsonObject::class.java)
                if (json["code"].asInt == 200 && json["data"] != null) {
                    val encryptedData = json["data"].asString
                    val decryptedData = decryptData(encryptedData)
                    val jo = Gson().fromJson(decryptedData, JsonObject::class.java)
                    Log.d("api", "解密后数据：$jo")
                    json.add("data", jo)
                }
                gsonConverter?.convert(json.toString().toResponseBody(responseBody.contentType()))
            }
        }
    }
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
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(responseConverterFactory)
            .addConverterFactory(primitiveToRequestBodyConverterFactory)
            .build()
    }

    private fun decryptData(str: String): String {
        val secretKey = SecretKeySpec(tokenHash.toByteArray(Charset.forName("UTF-8")), "AES")

        // 配置 Cipher
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)

        // 解密数据
        val encryptedBytes = android.util.Base64.decode(str, android.util.Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes, Charset.forName("UTF-8"))
    }

    fun <T> createService(cls: Class<T>): T {
        val service = retrofit.create(cls)
        return service
    }
}