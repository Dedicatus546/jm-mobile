package com.par9uet.jm.retrofit.model

import com.par9uet.jm.data.models.Category

data class ComicCategoryListResponse(
    val categories: List<CategoryListItem>,
    val blocks: List<BlockListItem>
) {
    data class CategoryListItem(
        val id: String,
        val name: String,
        val slug: String,
        val total_albums: String,
        val type: String?,
        val sub_categories: List<SubCategoryListItem>?
    ) {
        data class SubCategoryListItem(
            val CID: String,
            val name: String,
            val slug: String,
        )
    }

    data class BlockListItem(
        val title: String,
        val content: List<String>,
    )

    fun toCategoryList(): List<Category> {
        return categories.map {
            Category(
                id = it.id,
                name = it.name,
                slug = it.slug,
                type = it.type ?: "",
                subCategoryList = it.sub_categories?.map { s ->
                    Category.SubCategory(
                        id = s.CID,
                        name = s.name,
                        slug = s.slug
                    )
                } ?: listOf(),
            )
        }
    }

    fun toTagList(): List<String> {
        return blocks.flatMap {
            it.content
        }
    }
}