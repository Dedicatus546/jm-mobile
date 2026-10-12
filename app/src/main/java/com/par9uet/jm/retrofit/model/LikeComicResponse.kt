package com.par9uet.jm.retrofit.model

import kotlinx.serialization.Serializable

@Serializable
data class LikeComicResponse(
    val code: Int,
    val msg: String,
    val status: String
)