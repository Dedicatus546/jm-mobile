package com.par9uet.jm.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ComicChapter(
    val id: Int = 0,
    val name: String = "",
)