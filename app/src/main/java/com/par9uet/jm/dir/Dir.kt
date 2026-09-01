package com.par9uet.jm.dir

import android.content.Context
import com.par9uet.jm.utils.tryCreateDir
import java.io.File

fun getCommonCacheDir(context: Context) = tryCreateDir(File(context.cacheDir, "common"))
fun getCommonPicDecodeCacheDir(context: Context, comicId: Int) = tryCreateDir(File(context.cacheDir, "pic_decode/$comicId"))

fun getDownloadCacheDir(context: Context) = tryCreateDir(File(context.cacheDir, "download"))
fun getDownloadCoverDataDir(context: Context) = tryCreateDir(File(context.dataDir, "downloadcover"))