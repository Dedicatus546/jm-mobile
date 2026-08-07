package com.par9uet.jm.data.models

data class Category(
    val id: String,
    val name: String,
    val slug: String,
    val type: String,
    val subCategoryList: List<SubCategory>
) {
    data class SubCategory(
        val id: String,
        val name: String,
        val slug: String
    )
}
