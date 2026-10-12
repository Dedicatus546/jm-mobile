package com.par9uet.jm.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.size.Size
import com.par9uet.jm.data.models.ComicPicDecodeCompressLevel
import com.par9uet.jm.data.models.DbResult
import com.par9uet.jm.ui.provider.LocalMainActivity
import java.io.File
import java.io.OutputStream
import java.security.MessageDigest

fun tryCreateDir(dir: File): File {
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}

fun getComicPicCompressFormat(compressLevel: ComicPicDecodeCompressLevel? = null): Bitmap.CompressFormat {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (compressLevel == ComicPicDecodeCompressLevel.LOSS_LESS)
            Bitmap.CompressFormat.WEBP_LOSSLESS
        else
            Bitmap.CompressFormat.WEBP_LOSSY
    } else {
        Bitmap.CompressFormat.WEBP
    }
}

fun compressComicPic(
    bitmap: Bitmap,
    compressLevel: ComicPicDecodeCompressLevel,
    out: OutputStream
) {
    val format = getComicPicCompressFormat(compressLevel)
    // 无损下 quality 表示压损速度，而有损下 quality 表示压缩质量
    val quality = if (compressLevel == ComicPicDecodeCompressLevel.LOSS_LESS) 100 else 80
    bitmap.compress(format, quality, out)
}

@Composable
inline fun <reified VM : ViewModel> hiltActivityViewModel(): VM {
    return hiltViewModel<VM>(
        viewModelStoreOwner = LocalMainActivity.current
    )
}

fun formatFileSize(size: Long): String {
    return when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
        else -> "${size / (1024 * 1024 * 1024)} GB"
    }
}

fun String.sha256(): String {
    val digest = MessageDigest.getInstance("SHA-256")
        .digest(this.toByteArray(Charsets.UTF_8))
    val hexChars = CharArray(digest.size * 2)
    val hexTable = "0123456789abcdef"
    for (i in digest.indices) {
        val v = digest[i].toInt() and 0xFF
        hexChars[i * 2] = hexTable[v ushr 4]
        hexChars[i * 2 + 1] = hexTable[v and 0x0F]
    }
    return String(hexChars)
}

fun createComicCoverImageRequest(context: Context, comicId: Int, url: String): ImageRequest {
    val cacheKey = "cover-${comicId}"
    return ImageRequest.Builder(context)
        .data(url)
        .memoryCacheKey(cacheKey)
        .diskCacheKey(cacheKey)
        .build()
}

fun createAvatarImageRequest(context: Context, avatar: String, url: String): ImageRequest {
    val cacheKey = "avatar-${avatar}"
    return ImageRequest.Builder(context)
        .data(url)
        .memoryCacheKey(cacheKey)
        .diskCacheKey(cacheKey)
        .build()
}

fun createComicOriginalPicImageRequest(context: Context, url: String, comicId: Int): ImageRequest {
    val page = extractPageFromUrl(url)
    val originalCacheKey = "original-$comicId-$page"
    return ImageRequest.Builder(context)
        .data(url)
        .memoryCacheKey(originalCacheKey)
        .diskCacheKey(originalCacheKey)
        // 这里必须使用原始 size ，不然解密会有问题，出现白线
        .size { Size.ORIGINAL }
        .allowHardware(false)
        .build()
}

fun sanitizeFileName(name: String, replacement: Char = '_'): String {
    // 1. 替换 Android 硬性禁止的路径分隔符和 Windows 保留字符
    //    正则表达式含义：匹配 \ / : * ? " < > | 或控制字符
    val illegal = Regex("""[\\/:*?"<>|\u0000-\u001F]""")
    var safe = illegal.replace(name, replacement.toString())

    // 2. 防止路径穿越：去掉所有 . 和 .. 组合
    //    如 "../../etc" -> "______etc"（点被替换）
    //    这一步其实上一步已经覆盖了 '/' 和 '\'，但单独处理 '.' 更稳
    safe = safe.trimStart('.').trimEnd('.')

    // 3. 处理空名和保留名
    if (safe.isBlank()) safe = "unnamed"

    return safe
}

fun <T> DbResult<T>.getOrThrow(): T = when (this) {
    is DbResult.Success -> data
    is DbResult.Error -> {
        log("数据库操作", "错误，$message")
        throw Error(message)
    }
}

fun <T> DbResult<T>.runOrThrow(): Unit = when (this) {
    is DbResult.Error -> {
        log("数据库操作", "错误，$message")
        throw Error(message)
    }

    else -> {}
}