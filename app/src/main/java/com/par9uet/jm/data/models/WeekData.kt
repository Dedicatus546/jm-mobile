package com.par9uet.jm.data.models

data class WeekData(
    val categoryList: List<Pair<Int, String>> = listOf(),
    val typeList: List<Pair<Int, String>> = listOf()
)