package com.par9uet.jm.cache

import android.content.Context
import java.io.File

fun getCommonCacheDir(context: Context): File {
    return File(context.cacheDir, "common")
}

fun getCommonPicDecodeCacheDir(context: Context, comicId: Int): File {
    return File(context.cacheDir, "pic_decode/$comicId")
}

fun getDownloadDir(context: Context): File {
    return File(context.cacheDir, "download")
}