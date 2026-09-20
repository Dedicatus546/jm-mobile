package com.par9uet.jm.data.models

import com.par9uet.jm.database.model.DownloadStatus

data class DownloadFilter(
    val status: DownloadStatus,
)