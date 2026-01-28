package com.par9uet.jm.cache

import android.content.Context
import com.par9uet.jm.utils.tryCreateDir
import java.io.File

fun getCommonCacheDir(context: Context) = tryCreateDir(File(context.cacheDir, "common"))
fun getCommonPicDecodeCacheDir(context: Context, comicId: Int) = tryCreateDir(File(context.cacheDir, "pic_decode/$comicId"))
fun getDownloadDir(context: Context) = tryCreateDir(File(context.cacheDir, "download"))