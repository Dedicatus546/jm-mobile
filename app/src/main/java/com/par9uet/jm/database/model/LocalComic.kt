package com.par9uet.jm.database.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

enum class DownloadStatus {
    PENDING,
    PAUSE,
    DOWNLOADING,
    ERROR,
    COMPLETE
}

@Entity(tableName = "local_comic")
data class LocalComic(
    @PrimaryKey
    val comicId: Int,
    // 如果是单章的，就和 comicId 一样
    // 多章的则保存搜索结果的那一章，一般为第一话
    // 封面用该 belongComicId 读，这样多章可以公用一个 cover
    val belongComicId: Int,
    val name: String,
    val chapterName: String,
    val authorList: List<String>,
    val readCount: Int,
    val likeCount: Int,
    val tagList: List<String>? = null,
    val roleList: List<String>? = null,
    val workList: List<String>? = null,
    var comicKey: String = "",
    val status: DownloadStatus,  // PENDING, PAUSE, DOWNLOADING, ERROR, COMPLETE
    val progress: Float? = null,  // 下载进度
    val errorMessage: String? = null,  // 错误信息
    val zipPath: Uri? = null,  // 完成后的压缩包
    val zipMd5: String? = null,
    val createTime: Long,
)