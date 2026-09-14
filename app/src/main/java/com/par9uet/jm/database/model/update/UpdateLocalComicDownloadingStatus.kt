package com.par9uet.jm.database.model.update

import com.par9uet.jm.database.model.DownloadStatus

data class UpdateLocalComicDownloadingStatus(
    val comicId: Int,
    val status: DownloadStatus = DownloadStatus.DOWNLOADING,
)