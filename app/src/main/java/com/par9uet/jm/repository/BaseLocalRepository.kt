package com.par9uet.jm.repository

import com.par9uet.jm.data.models.DbResult
import com.par9uet.jm.utils.log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class BaseLocalRepository {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): DbResult<T> {
        return try {
            val response = withContext(Dispatchers.IO) {
                apiCall()
            }
            DbResult.Success(response)
        } catch (e: Throwable) {
            handleException(e)
        }
    }

    fun <T> safeSyncApiCall(apiCall: () -> T): DbResult<T> {
        return try {
            val response = apiCall()
            DbResult.Success(response)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            handleException(e)
        }
    }

    private fun handleException(e: Throwable): DbResult.Error {
        log("获取数据失败，${e.stackTraceToString()}")
        return DbResult.Error(
            e.message ?: "读取数据错误"
        )
    }
}