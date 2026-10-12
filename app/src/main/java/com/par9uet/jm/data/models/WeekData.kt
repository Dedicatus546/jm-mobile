package com.par9uet.jm.data.models

import kotlinx.serialization.Serializable

@Serializable
data class WeekData(
    val categoryList: List<Pair<String, String>> = listOf(),
    val typeList: List<Pair<String, String>> = listOf()
)