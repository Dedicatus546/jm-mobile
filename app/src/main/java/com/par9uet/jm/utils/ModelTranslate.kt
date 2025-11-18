package com.par9uet.jm.utils

import com.par9uet.jm.data.models.Comic

fun createComic(
    id: Int,
    name: String,
    authorList: List<String>,
): Comic {
    return Comic(
        id = id,
        name = name,
        authorList = authorList,
        description = "",
        readCount = 0,
        likeCount = 0,
        commentCount = 0,
        tagList = listOf(),
        roleList = listOf(),
        workList = listOf(),
        isLike = false,
        isCollect = false,
        relativeComicList = listOf(),
        comicChapterList = listOf(),
        price = 0,
        isBuy = false,
    )
}