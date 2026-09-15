package com.par9uet.jm.utils

import android.content.Context
import java.io.File

fun getDownloadCacheDir(context: Context) = tryCreateDir(File(context.cacheDir, "download"))
fun getDownloadCoverDataDir(context: Context) = tryCreateDir(File(context.dataDir, "downloadcover"))