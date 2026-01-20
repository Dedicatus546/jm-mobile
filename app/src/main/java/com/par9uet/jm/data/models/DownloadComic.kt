package com.par9uet.jm.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_comics")
data class DownloadComic(
    @PrimaryKey
    val id: Int,
    val name: String,
    val authorList: List<String>,
    val coverPath: String,
    val picPathList: List<String>,
    val zipPath: String,
    val progress: Float,
    val status: String, // pending || downloading || complete
)