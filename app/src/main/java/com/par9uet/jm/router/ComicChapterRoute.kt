package com.par9uet.jm.router

import com.par9uet.jm.data.models.ComicChapter
import kotlinx.serialization.Serializable

@Serializable
data class ComicChapterRoute(
    val comicChapterList: List<ComicChapter>
)