package com.par9uet.jm.repository

import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.retrofit.service.ProxyApiService
import com.par9uet.jm.utils.log
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Singleton
class ProxyApiRepository @Inject constructor(
    private val service: ProxyApiService,
) {
    suspend fun getApiList(): NetworkResult<List<String>> {
        return try {
            val response = service.getApiList()
            NetworkResult.Success(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun handleException(e: Exception): NetworkResult.Error {
        log(e.stackTraceToString())
        return when (e) {
            is SocketTimeoutException -> NetworkResult.Error("网络连接超时")
            is ConnectException -> NetworkResult.Error("网络连接失败")
            is UnknownHostException -> NetworkResult.Error("网络不可用")

            else -> NetworkResult.Error(
                e.message ?: "未知错误"
            )
        }
    }
}