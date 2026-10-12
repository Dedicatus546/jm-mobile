package com.par9uet.jm.data.models

import kotlinx.serialization.Serializable

@Serializable
data class HomeComicSwiperItem(
    val id: String,
    val title: String,
    val list: List<Comic>
)
