package com.par9uet.jm.utils

import android.content.Context
import java.io.File

fun getDownloadCacheDir(context: Context) = tryCreateDir(File(context.cacheDir, "download"))
fun getDownloadComicCoverDir(context: Context) = tryCreateDir(File(context.dataDir, "download_cover"))
fun getDownloadComicPicDir(context: Context, comicId: Int) =
    tryCreateDir(File(context.dataDir, "download_comic/$comicId"))