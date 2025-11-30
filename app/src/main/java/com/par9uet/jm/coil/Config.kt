package com.par9uet.jm.coil

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import com.par9uet.jm.cache.getCommonCacheDir

fun createAsyncImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .diskCache {
            DiskCache.Builder()
                .directory(getCommonCacheDir(context)) // 自定义目录
                .maxSizeBytes(200L * 1024 * 1024) // 200MB
                .build()
        }
        .build()
}

fun createPicImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .diskCache {
            DiskCache.Builder()
                .directory(getCommonCacheDir(context)) // 自定义目录
                .maxSizeBytes(200L * 1024 * 1024) // 200MB
                .build()
        }
        .build()
}