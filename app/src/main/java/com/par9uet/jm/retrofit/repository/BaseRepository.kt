package com.par9uet.jm.retrofit.repository

import android.util.Log
import coil.network.HttpException
import com.par9uet.jm.retrofit.model.ResponseWrapper
import com.par9uet.jm.retrofit.model.NetWorkResult
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseRepository {
    suspend fun <T> safeApiCall(apiCall: suspend () -> ResponseWrapper<T>): NetWorkResult<T> {
        return try {
            val response = apiCall()
            if (response.code == 200) {
                response.data?.let { data ->
                    NetWorkResult.Success(data)
                } ?: NetWorkResult.Success(Unit as T)
            } else {
                NetWorkResult.Error(response.errorMsg ?: "接口报错", response.code)
            }
        } catch (e: Exception) {
            Log.d("api", e.stackTraceToString())
            when (e) {
                is SocketTimeoutException -> NetWorkResult.Error("网络连接超时")
                is ConnectException -> NetWorkResult.Error("网络连接失败")
                is UnknownHostException -> NetWorkResult.Error("网络不可用")
                is HttpException -> {
                    val errMsg = when (e.response.code) {
                        401 -> "未授权，请重新登录"
                        else -> "网络错误：${e.response.code}"
                    }
                    NetWorkResult.Error(errMsg)
                }

                else -> NetWorkResult.Error(
                    e.message ?: "未知错误"
                )
            }
        }
    }

    suspend fun <T> safeApiCallWithLoading(
        apiCall: suspend () -> ResponseWrapper<T>,
        onLoading: (Boolean) -> Unit = {}
    ): NetWorkResult<T> {
        return try {
            onLoading(true)
            safeApiCall(apiCall)
        } finally {
            onLoading(false)
        }
    }
}