package com.par9uet.jm.retrofit.model

data class ResponseWrapper<T>(
    val code: Int,
    val data: T?,
    val errorMsg: String?,
)