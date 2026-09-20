package com.par9uet.jm.database.model.update

import com.par9uet.jm.data.models.DownloadStatus

data class UpdateLocalComicDownloadingStatus(
    val comicId: Int,
    val status: DownloadStatus = DownloadStatus.DOWNLOADING,
)