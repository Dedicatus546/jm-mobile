package com.par9uet.jm.retrofit.model

import com.par9uet.jm.data.models.WeekData
import kotlinx.serialization.Serializable

@Serializable
data class WeekResponse(
    val categories: List<CategoryItem> = listOf(),
    val type: List<TypeItem>
) {

    @Serializable
    data class CategoryItem(
        val id: String,
        val time: String,
        val title: String,
    )


    @Serializable
    data class TypeItem(
        val id: String,
        val title: String
    )

    fun toWeekData() = WeekData(
        categoryList = categories.map { it.id to it.time },
        typeList = type.map { it.id to it.title }
    )
}