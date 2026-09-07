package com.par9uet.jm.dir

import android.content.Context
import com.par9uet.jm.utils.tryCreateDir
import java.io.File

fun getComicCoverCacheDir(context: Context) = tryCreateDir(File(context.cacheDir, "cover"))
fun getComicPicCacheDir(context: Context) = tryCreateDir(File(context.cacheDir, "pic"))
fun getComicPicDecodeCacheDir(context: Context, comicId: Int? = null) =
    tryCreateDir(File(context.cacheDir, "pic_decode${if (comicId == null) "" else "/$comicId"}"))

fun getDownloadCacheDir(context: Context) = tryCreateDir(File(context.cacheDir, "download"))
fun getDownloadCoverDataDir(context: Context) = tryCreateDir(File(context.dataDir, "downloadcover"))