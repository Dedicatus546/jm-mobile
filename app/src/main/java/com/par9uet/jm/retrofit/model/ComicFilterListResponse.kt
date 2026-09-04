package com.par9uet.jm.retrofit.model

import com.par9uet.jm.data.models.Comic
import kotlinx.serialization.Serializable

@Serializable
data class ComicFilterListResponse(
    val total: String,
    val content: List<ContentListItem>
) {
    @Serializable
    data class ContentListItem(
        val id: String,
        val author: String,
        val description: String? = null,
        val name: String,
        // val image: String,
        // val category: Category,
        // val category_sub: Category,
        // val liked: Boolean,
        // val is_favorite: Boolean,
        // val update_at: Int,
    ) {
        @Serializable
        data class Category(
            val id: String?,
            val title: String?
        )
    }

    fun toComicList(): List<Comic> {
        return content.map {
            Comic(
                id = it.id.toInt(),
                name = it.name,
                authorList = listOf(it.author),
                description = it.description,
            )
        }
    }
}