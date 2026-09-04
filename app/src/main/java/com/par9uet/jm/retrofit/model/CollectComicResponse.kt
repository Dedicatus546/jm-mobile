package com.par9uet.jm.retrofit.model

import kotlinx.serialization.Serializable

@Serializable
data class CollectComicResponse(
    val msg: String,
    val status: String,
    val type: String,
)