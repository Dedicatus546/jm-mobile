package com.par9uet.jm.database.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.par9uet.jm.utils.extractPageFromUrl

@Entity(
    tableName = "local_comic_pic",
    foreignKeys = [
        ForeignKey(
            entity = LocalComic::class,
            parentColumns = ["comicId"],
            childColumns = ["comicId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["comicId"])],
    primaryKeys = ["comicId", "page"]
)
data class LocalComicPic(
    val comicId: Int,
    val page: String,
    val url: String,
    val isComplete: Boolean = false,
    val path: Uri,
    val md5: String? = null,
)