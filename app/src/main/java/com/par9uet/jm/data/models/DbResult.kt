package com.par9uet.jm.data.models

sealed class DbResult<out T> {
    data class Success<T>(val data: T) : DbResult<T>()
    data class Error(val message: String) : DbResult<Nothing>()
}