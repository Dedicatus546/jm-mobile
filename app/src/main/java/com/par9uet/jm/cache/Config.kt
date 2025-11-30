package com.par9uet.jm.cache

import android.content.Context
import java.io.File

private const val COMIC_PIC_DIR = "pic"
private const val COMMON_DIR = "other"

fun getCommonCacheDir(context: Context): File {
    return File(context.cacheDir, COMMON_DIR)
}

fun getCommonPicDecodeCacheDir(context: Context, comicId: Int): File {
    return File(context.cacheDir, "pic_decode/$comicId")
}