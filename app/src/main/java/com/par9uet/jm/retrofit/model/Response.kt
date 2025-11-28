package com.par9uet.jm.retrofit.model

sealed interface Response<T>

data class CommonResponse<T>(
    val code: Int,
    val data: T?,
    val errorMsg: String?,
): Response<T>

data class HtmlResponse(val value: String): Response<String>