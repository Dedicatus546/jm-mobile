package com.par9uet.jm.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap

private val seedMap = listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)

private data class DecodeContext(
    val comicId: Int,
    val originalSrc: String,
    val scrambleId: Int,
    val speed: String,
    val page: String
)

private fun cLog(msg: String, ctx: DecodeContext) {
    log("解码图片数据", "$msg\n$ctx")
}

fun decodeComicPicBitmap(
    originalSrc: String,
    originalBitmap: Bitmap,
    comicId: Int,
    scrambleId: Int,
    speed: String,
    page: String
): Bitmap {
    val ctx = DecodeContext(comicId, originalSrc, scrambleId, speed, page)
    if (isGif(originalSrc) || comicId <= scrambleId || speed == "1") {
        when {
            isGif(originalSrc) -> cLog("gif 格式无需解密", ctx)
            comicId <= scrambleId -> cLog("comicId <= scrambleId 无需解密", ctx)
            speed == "1" -> cLog("speed == \"1\"无需解密", ctx)
        }
        // 无需解密
        return originalBitmap
    }

    val naturalWidth = originalBitmap.width
    val naturalHeight = originalBitmap.height
    val seed = calculateSeed(comicId, page)
    cLog("seed 为 $seed", ctx)
    val remainder = naturalHeight % seed

    val decodedBitmap =
        createBitmap(naturalWidth, naturalHeight)
    val canvas = Canvas(decodedBitmap.asImageBitmap())
    val paint = Paint().apply {
        this.isAntiAlias = false
    }
    val originImageBitmap = originalBitmap.asImageBitmap()

    for (i in 0 until seed) {
        var height = naturalHeight / seed
        var dy = height * i
        val sy = naturalHeight - height * (i + 1) - remainder
        if (i == 0) {
            height += remainder
        } else {
            dy += remainder
        }

        val srcOffset = IntOffset(0, sy)
        val srcSize = IntSize(naturalWidth, height)
        val destOffset = IntOffset(0, dy)
        val destSize = IntSize(naturalWidth, height)

        canvas.drawImageRect(
            originImageBitmap,
            srcOffset,
            srcSize,
            destOffset,
            destSize,
            paint
        )
    }

    return decodedBitmap
}

private fun calculateSeed(comicId: Int, pageStr: String): Int {
    val key = "$comicId$pageStr"
    val keyMd5 = md5(key)
    var charCodeOfLastChar = keyMd5.last().code
    val left = 268850
    val right = 421925

    when {
        comicId in left..right -> charCodeOfLastChar %= 10
        comicId >= right + 1 -> charCodeOfLastChar %= 8
    }

    return seedMap.getOrNull(charCodeOfLastChar) ?: 10
}

private fun isGif(originalSrc: String): Boolean {
    return originalSrc.endsWith(".gif")
}

fun extractPageFromUrl(originSrc: String): String {
    return originSrc.substringAfterLast('/').substringBeforeLast('.')
}