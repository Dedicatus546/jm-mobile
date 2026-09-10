package com.par9uet.jm.utils

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
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

fun getComicPicCompressFormat(compressLevel: String? = null): Bitmap.CompressFormat {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (compressLevel == "lossless") Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.WEBP_LOSSY
    } else {
        Bitmap.CompressFormat.WEBP
    }
}

fun compressComicPic(bitmap: Bitmap, compressLevel: String, out: OutputStream) {
    val format = getComicPicCompressFormat(compressLevel)
    val quality = if (compressLevel == "lossless") 50 else 80
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