package com.par9uet.jm.utils

import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.User
import kotlin.Int

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

fun createUser(): User {
    return User(
        id = 0,
        username = "",
        avatar = "",
        level = 0,
        levelName = "",
        currentLevelExp = 0,
        nextLevelExp = 0,
        currentCollectCount = 0,
        maxCollectCount = 0,
        jCoin = 0,
    )
}