package com.par9uet.jm.retrofit.model

import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicChapter
import kotlinx.serialization.Serializable

@Serializable
data class ComicDetailResponse(
    val id: Int,
    val name: String,
    val description: String,
    val author: List<String>,
    val total_views: Int,
    val likes: Int,
    val comment_total: Int,
    val tags: List<String>,
    val actors: List<String>,
    val works: List<String>,
    val is_favorite: Boolean,
    val liked: Boolean,
    val related_list: List<RelatedListItemResponse>,
    val series: List<SeriesListItemResponse>,
    // val series_id: String,
    // val price: String,
    // val purchased: Boolean,
) {
    @Serializable
    data class RelatedListItemResponse(
        val id: String,
        val name: String,
        val author: String,
        val image: String,
    )

    @Serializable
    data class SeriesListItemResponse (
        val id: String,
        val name: String,
        val sort: String,
    )

    fun toComic(): Comic {
        return Comic(
            id = id,
            name = name,
            authorList = author,
            description = description,
            readCount = total_views,
            likeCount = likes,
            commentCount = comment_total,
            tagList = tags,
            roleList = actors,
            workList = works,
            isLike = liked,
            isCollect = is_favorite,
            relateComicList = related_list.map {
                Comic(
                    id = it.id.toInt(),
                    name = it.name,
                    authorList = listOf(it.author),
                )
            },
            comicChapterList = series.map { ComicChapter(it.id.toInt(), it.name) },
        )
    }
}