package com.par9uet.jm.retrofit.model

import com.par9uet.jm.data.models.Comic
import kotlinx.serialization.Serializable

@Serializable
data class UserHistoryComicListResponse(
    val list: List<ListItem>,
    val total: Int,
) {

    @Serializable
    data class ListItem(
        val id: String,
        val author: String,
        val description: String?,
        val name: String,
        val image: String,
        val category: Category,
        val category_sub: Category,
    ) {

        @Serializable
        data class Category(
            val id: String?,
            val title: String?
        )
    }

    fun toComicList(): List<Comic> {
        return list.map {
            Comic(
                id = it.id.toInt(),
                name = it.name,
                authorList = listOf(it.author),
            )
        }
    }
}