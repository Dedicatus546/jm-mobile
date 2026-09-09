package com.par9uet.jm.retrofit

import com.par9uet.jm.retrofit.interceptor.ToastInterceptor
import com.par9uet.jm.utils.json
import jakarta.inject.Inject
import jakarta.inject.Singleton
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * 这个类主要用于一些外部 API ，目前是一个用于获取 api 列表
 */
@Singleton
class ProxyRetrofit @Inject constructor() {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://proxy-api.prohibitorum.top") // 占位，会在 okhttp 的拦截器中进行动态替换
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            // .addConverterFactory(PrimitiveToRequestBodyConverterFactory())
            .build()
    }

    fun <T> createService(cls: Class<T>): T {
        val service = retrofit.create(cls)
        return service
    }
}