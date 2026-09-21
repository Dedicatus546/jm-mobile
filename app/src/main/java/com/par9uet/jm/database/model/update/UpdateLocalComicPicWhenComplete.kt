package com.par9uet.jm.database.model.update

import android.net.Uri

data class UpdateLocalComicPicWhenComplete(
    val comicId: Int,
    val page: String,
    val md5: String,
    val isComplete: Boolean = true,
)