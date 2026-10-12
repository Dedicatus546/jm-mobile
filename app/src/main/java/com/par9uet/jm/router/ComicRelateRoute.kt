package com.par9uet.jm.router

import com.par9uet.jm.data.models.Comic
import kotlinx.serialization.Serializable

@Serializable
data class ComicRelateRoute(
    val relateComicList: List<Comic>
)