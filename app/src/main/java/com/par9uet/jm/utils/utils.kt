package com.par9uet.jm.utils

import android.graphics.Bitmap
import android.os.Build
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