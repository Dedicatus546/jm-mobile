package com.par9uet.jm.retrofit

import com.par9uet.jm.retrofit.converter.PrimitiveToRequestBodyConverterFactory
import com.par9uet.jm.retrofit.interceptor.BaseUrlInterceptor
import com.par9uet.jm.retrofit.interceptor.DecryptInterceptor
import com.par9uet.jm.retrofit.interceptor.InitInterceptor
import com.par9uet.jm.retrofit.interceptor.ToastInterceptor
import com.par9uet.jm.retrofit.interceptor.TokenInterceptor
import com.par9uet.jm.storage.CookieStorage
import com.par9uet.jm.task.AppInitTask
import com.par9uet.jm.task.AppTaskInfo
import com.par9uet.jm.utils.json
import com.par9uet.jm.utils.log
import jakarta.inject.Inject
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class Retrofit @Inject constructor(
    baseUrlInterceptor: BaseUrlInterceptor,
    toastInterceptor: ToastInterceptor,
    initInterceptor: InitInterceptor,
    private val cookieStorage: CookieStorage
) : AppInitTask {
    private val appTaskInfo = AppTaskInfo(
        taskName = "Retrofit 配置",
        sort = 1
    )
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
            .addInterceptor(initInterceptor)
            .addInterceptor(baseUrlInterceptor)
            .addInterceptor(TokenInterceptor())
            .addInterceptor(toastInterceptor)
            .addInterceptor(DecryptInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .cookieJar(cookieJar)
            .build()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://placeholder.com/") // 占位，会在 okhttp 的拦截器中进行动态替换
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addConverterFactory(PrimitiveToRequestBodyConverterFactory())
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