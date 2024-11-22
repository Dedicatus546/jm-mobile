package com.par9uet.jm.retrofit

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
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
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

private fun md5(str: String): String {
    return MessageDigest.getInstance("MD5").digest(str.toByteArray())
        .joinToString("") { "%02x".format(it) }.lowercase()
}

data class RespWrapper<T>(val code: Int, val errorMsg: String?, val data: T)

class ClientException(msg: String) : Exception(msg)

class LoginCookieJar : CookieJar {

    private var cookieStore: MutableList<Cookie> = mutableListOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore = cookies.toMutableList()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore
    }
}

object Client {
    var currentApi = "https://www.jmapiproxyxxx.vip"
    val apiList = listOf("https://www.jmapiproxyxxx.vip")

    private val ts = System.currentTimeMillis() / 1000
    private const val version = "1.7.4"
    private const val tokenSecret = "185Hcomic3PAPP7R"
    private val tokenHash = md5("${ts}${tokenSecret}")
    private lateinit var retrofit: Retrofit
    private val loginCookieJar = LoginCookieJar()
    private val stringToRequestBodyConverterFactory = object : Converter.Factory() {
        override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<*, RequestBody>? {
            if (type == String::class.java) {
                return Converter<String, RequestBody> { value ->
                    value.toRequestBody("text/plain".toMediaType())
                }
            }
            return null
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
                    val decryptedData = this@Client.decryptData(encryptedData)
                    json.add("data", Gson().fromJson(decryptedData, JsonObject::class.java))
                }
                gsonConverter?.convert(json.toString().toResponseBody(responseBody.contentType()))
            }
        }
    }
    private val okHttpClient =
        OkHttpClient.Builder().addInterceptor(tokenInterceptor)
            .cookieJar(loginCookieJar).build()
    private var serviceMap = mutableMapOf<Class<*>, Any>()

    init {
        buildRetrofit()
    }

    private fun buildRetrofit() {
        retrofit = Retrofit.Builder().baseUrl(currentApi).client(okHttpClient)
            .addConverterFactory(responseConverterFactory)
            .addConverterFactory(stringToRequestBodyConverterFactory).build()
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

    fun changeApi(api: String) {
        if (api !in apiList) {
            throw ClientException("更改的 api 不在可修改的 api 列表中")
        }
        currentApi = api
        buildRetrofit()
        serviceMap.clear()
    }

    fun <T> create(cls: Class<T>): T {
        if (serviceMap.containsKey(cls)) {
            return serviceMap[cls] as T
        }
        val service = retrofit.create(cls)
        serviceMap[cls] = service as Any
        return service
    }
}