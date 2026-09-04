package com.par9uet.jm.retrofit.model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseWrapper<T>(
    val code: Int,
    val data: T? = null,
    val errorMsg: String? = null
) {
}