package com.par9uet.jm.constant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Pending
import com.par9uet.jm.data.models.DownloadStatus

val downloadStatusTextMap = mapOf(
    DownloadStatus.COMPLETE to "完成",
    DownloadStatus.PENDING to "等待中",
    DownloadStatus.DOWNLOADING to "下载中",
    DownloadStatus.ERROR to "出错",
    DownloadStatus.PAUSE to "暂停"
)

val downloadStatusIconMap = mapOf(
    DownloadStatus.PENDING to Icons.Default.Pending,
    DownloadStatus.COMPLETE to Icons.Default.DownloadDone,
    DownloadStatus.DOWNLOADING to Icons.Default.Downloading,
    DownloadStatus.ERROR to Icons.Default.Error,
    DownloadStatus.PAUSE to Icons.Default.Pause,
)