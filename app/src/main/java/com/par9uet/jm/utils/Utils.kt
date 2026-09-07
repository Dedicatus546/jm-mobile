package com.par9uet.jm.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import coil.ImageLoader
import coil.disk.DiskCache
import com.par9uet.jm.ui.provider.LocalMainActivity
import java.io.File
import java.io.OutputStream

fun tryCreateDir(dir: File): File {
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}

fun getComicPicCompressFormat(): Bitmap.CompressFormat {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Bitmap.CompressFormat.WEBP_LOSSY
    } else {
        Bitmap.CompressFormat.WEBP
    }
}

fun compressComicPic(bitmap: Bitmap, out: OutputStream) {
    bitmap.compress(getComicPicCompressFormat(), 85, out)
}

fun createAsyncImageLoader(context: Context, dir: File): ImageLoader {
    return ImageLoader.Builder(context)
        .diskCache {
            DiskCache.Builder()
                .directory(dir)
                .maxSizeBytes(1024L * 1024 * 1024) // 200MB
                .build()
        }
        .build()
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