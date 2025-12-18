package com.par9uet.jm.retrofit

import com.par9uet.jm.retrofit.converter.PrimitiveToRequestBodyConverterFactory
import com.par9uet.jm.retrofit.converter.ResponseConverterFactory
import com.par9uet.jm.retrofit.interceptor.TokenInterceptor
import com.par9uet.jm.storage.CookieStorage
import com.par9uet.jm.task.AppInitTask
import com.par9uet.jm.task.AppTaskInfo
import com.par9uet.jm.utils.log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class Retrofit(
    tokenInterceptor: TokenInterceptor,
    private val scalarsConverterFactory: ScalarsConverterFactory,
    private val responseConverterFactory: ResponseConverterFactory,
    private val primitiveToRequestBodyConverterFactory: PrimitiveToRequestBodyConverterFactory,
    private val cookieStorage: CookieStorage
) : AppInitTask {
    private val appTaskInfo = AppTaskInfo(
        taskName = "Retrofit 配置",
        sort = 1
    )
    private var apiList = listOf("https://www.cdnzack.cc")
    private var baseUrl = apiList[0]
    private var cookieList = listOf<Cookie>()
    private val cookieJar = object : CookieJar {

        override fun saveFromResponse(
            url: HttpUrl,
            cookies: List<Cookie>
        ) {
            cookieList =
                (cookieList + cookies).associateBy { "${it.domain}:${it.path}:${it.name}" }.values.toList()
            cookieStorage.set(cookieList)
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieList
        }

    }
    private val okHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .cookieJar(cookieJar)
            .build()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(responseConverterFactory)
            .addConverterFactory(primitiveToRequestBodyConverterFactory)
            .build()
    }

    fun <T> createService(cls: Class<T>): T {
        val service = retrofit.create(cls)
        return service
    }

    fun clearCookie() {
        cookieList = listOf()
    }

    override suspend fun init() {
        log("Retrofit 开始初始化")
        log("恢复 Retrofit Cookie")
        cookieList = cookieStorage.get()
        log("已恢复 Retrofit Cookie")
        log("Retrofit 初始化结束")
    }

    override fun getAppTaskInfo(): AppTaskInfo = appTaskInfo
}