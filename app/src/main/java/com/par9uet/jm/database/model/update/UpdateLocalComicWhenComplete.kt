package com.par9uet.jm.database.model.update

import android.net.Uri
import com.par9uet.jm.database.model.DownloadStatus

data class UpdateLocalComicWhenComplete(
    val comicId: Int,
    val zipPath: Uri,
    val zipMd5: String,
    val status: DownloadStatus = DownloadStatus.COMPLETE
)