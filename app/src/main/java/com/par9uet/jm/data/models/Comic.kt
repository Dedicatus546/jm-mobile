package com.par9uet.jm.data.models

data class Comic(
    val id: Int,
    val name: String,
    val author: String,
)

data class PromoteComicListItem(
    val id: String,
    val title: String,
    val list: List<Comic>
)