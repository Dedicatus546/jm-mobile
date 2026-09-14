package com.par9uet.jm.database.model.update

import com.par9uet.jm.database.model.DownloadStatus

data class UpdateLocalComicErrorStatus(
    val comicId: Int,
    val errorMessage: String,
    val status: DownloadStatus = DownloadStatus.ERROR,
)