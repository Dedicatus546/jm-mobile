package com.par9uet.jm.retrofit.model

import kotlinx.serialization.Serializable

@Serializable
data class CommentComicResponse(
    val msg: String,
    val status: String,
    val aid: Int,
    val cid: Int,
    val spoiler: String,
)