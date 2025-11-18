package com.par9uet.jm.retrofit.model

sealed class NetWorkResult<T> {
    data class Success<T>(val data: T): NetWorkResult<T>()
    data class Error<T>(val message: String, val code: Int = -1): NetWorkResult<T>()
    data class Loading<T>(val isLoading: Boolean = false): NetWorkResult<T>()
}