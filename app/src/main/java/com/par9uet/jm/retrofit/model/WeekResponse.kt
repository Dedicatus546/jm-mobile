package com.par9uet.jm.retrofit.model

import com.par9uet.jm.data.models.WeekData

data class WeekResponse(
    val categories: List<CategoryItem> = listOf(),
    val types: List<TypeItem>
) {
    data class CategoryItem(
        val id: Int,
        val time: String,
        val title: String,
    )

    data class TypeItem(
        val id: Int,
        val title: String
    )

    fun toWeekData() = WeekData(
        categoryList = categories.map { it.id to it.time },
        typeList = types.map { it.id to it.title }
    )
}