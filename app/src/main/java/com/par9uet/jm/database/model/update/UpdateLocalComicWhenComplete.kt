package com.par9uet.jm.database.model.update

import android.net.Uri

data class UpdateLocalComicWhenComplete(
    val comicId: Int,
    val zipPath: Uri,
    val zipMd5: String,
    val status: String = "complete"
)