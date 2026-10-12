package com.par9uet.jm.retrofit.interceptor

import com.par9uet.jm.store.ToastManager
import jakarta.inject.Inject
import jakarta.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class ToastInterceptor @Inject constructor(
    private val toastManager: ToastManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (!response.isSuccessful) {
            toastManager.show("网络错误: ${response.code}")
        }
        return response
    }
}
