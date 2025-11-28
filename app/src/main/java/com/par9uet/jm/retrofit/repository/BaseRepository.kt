package com.par9uet.jm.retrofit.repository

import android.util.Log
import coil.network.HttpException
import com.par9uet.jm.retrofit.model.CommonResponse
import com.par9uet.jm.retrofit.model.HtmlResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseRepository {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetWorkResult<T> {
        return try {
            when(val response = apiCall()) {
                is CommonResponse<T> -> {
                    if (response.code == 200) {
                        response.data?.let { data ->
                            NetWorkResult.Success(data)
                        } ?: NetWorkResult.Success(Unit as T)
                    } else {
                        NetWorkResult.Error(response.errorMsg ?: "接口报错", response.code)
                    }
                }
                is HtmlResponse -> {
                    NetWorkResult.Success(response.value as T)
                }
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
}