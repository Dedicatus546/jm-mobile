package com.par9uet.jm.retrofit.model

sealed class NetWorkResult<out T> {
    data class Success<T>(val data: T) : NetWorkResult<T>()
    data class Error(val message: String, val code: Int = -1) : NetWorkResult<Nothing>()
}