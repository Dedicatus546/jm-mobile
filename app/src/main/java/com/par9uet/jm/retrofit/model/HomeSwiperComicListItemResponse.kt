package com.par9uet.jm.retrofit.model

import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.HomeComicSwiperItem
import kotlinx.serialization.Serializable

@Serializable
data class HomeSwiperComicListItemResponse(
    val id: String,
    val title: String,
    val content: List<ListItem>,
    // val slug: String,
    val type: String,
    // val filter_val: String,
) {
    @Serializable
    data class ListItem(
        val id: String,
        val author: String,
        val description: String = "",
        val name: String,
        // val image: String,
        // val category: Category? = null,
        // val category_sub: Category? = null,
        // val liked: Boolean = false,
        // val is_favorite: Boolean = false,
        // val update_at: Int,
    ) {
        @Serializable
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
                )
            }
        )
    }
}