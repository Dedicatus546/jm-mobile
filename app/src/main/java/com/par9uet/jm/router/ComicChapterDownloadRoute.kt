package com.par9uet.jm.router

import com.par9uet.jm.data.models.ComicChapter
import kotlinx.serialization.Serializable

@Serializable
data class ComicChapterDownloadRoute(
    val comicChapterList: List<ComicChapter>
)