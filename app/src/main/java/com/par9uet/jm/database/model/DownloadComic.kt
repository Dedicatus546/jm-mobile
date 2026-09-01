package com.par9uet.jm.database.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "download_comics")
@TypeConverters(DownloadComic.UriConverter::class)
data class DownloadComic(
    @PrimaryKey
    val id: Int,
    val name: String,
    val authorList: List<String>,
    val readCount: Int,
    val likeCount: Int,
    val tagList: List<String>,
    val roleList: List<String>,
    val workList: List<String>,
    var comicKey: String = "",

    val status: String,  // PENDING, DOWNLOADING, ERROR, COMPLETE
    val progress: Float? = null,  // 下载进度
    val errorMessage: String? = null,  // 错误信息
    val zipPath: Uri? = null,  // 完成后的压缩包
    val zipMd5: String? = null,
    val createTime: Long,
) {
    class UriConverter {
        @TypeConverter
        fun fromUri(uri: Uri?): String? {
            return uri?.toString()
        }

        @TypeConverter
        fun toUri(uriString: String?): Uri? {
            return uriString?.let { Uri.parse(it) }
        }
    }
}