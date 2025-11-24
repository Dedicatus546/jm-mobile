package com.par9uet.jm.retrofit.model

import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.HomeComicSwiperItem

class HomeSwiperComicListItemResponse(
    val id: String,
    val title: String,
    val slug: String,
    val type: String,
    val filter_val: String,
    val content: List<ListItem>
) {
    data class ListItem(
        val id: String,
        val author: String,
        val description: String?,
        val name: String,
        val image: String,
        val category: Category,
        val category_sub: Category,
        val liked: Boolean,
        val is_favorite: Boolean,
        val update_at: Int,
    ) {
        data class Category(
            val id: String?,
            val title: String?
        )
    }

    fun toHomeComicSwiperItem(): HomeComicSwiperItem {
        return HomeComicSwiperItem(
            id = id,
            title = title,
            list = content.map {
                Comic(
                    id = it.id.toInt(),
                    name = it.name,
                    authorList = listOf(it.author),
                    description = it.description ?: "",
                    readCount = 0,
                    likeCount = 0,
                    commentCount = 0,
                    tagList = listOf(),
                    roleList = listOf(),
                    workList = listOf(),
                    isLike = false,
                    isCollect = false,
                    relativeComicList = listOf(),
                    comicChapterList = listOf(),
                    price = 0,
                    isBuy = false,
                )
            }
        )
    }
}