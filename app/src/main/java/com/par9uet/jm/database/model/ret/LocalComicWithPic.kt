package com.par9uet.jm.database.model.ret

import androidx.room.Embedded
import androidx.room.Relation
import com.par9uet.jm.database.model.LocalComic
import com.par9uet.jm.database.model.LocalComicPic

data class LocalComicWithPic(
    @Embedded
    val localComic: LocalComic,
    @Relation(
        parentColumn = "comicId",
        entityColumn = "comicId"
    )
    val localComicPicList: List<LocalComicPic>
)