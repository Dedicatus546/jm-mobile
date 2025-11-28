package com.par9uet.jm.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.Coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.par9uet.jm.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ComicPicImageViewModel(
    private val context: Context
) : ViewModel() {
    companion object {
        private val seedMap = listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)
        private const val LEFT = 268850
        private const val RIGHT = 421925
    }

    var loading by mutableStateOf(false)
    private var originSrc by mutableStateOf("")
    var decodeSrc by mutableStateOf("")
    private var comicId by mutableStateOf(-1)

    private val cacheDir by lazy {
        // 获取应用缓存目录
        context.externalCacheDir ?: File("/cache")
    }

    fun decode(src: String, comicId: Int) {
        loading = true
        originSrc = src
        this.comicId = comicId
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                decodeImage()
                loading = false
            }
        }
    }

    private suspend fun decodeImage() {
        if (comicId <= LEFT) {
            decodeSrc = originSrc
            Log.d("decode image", "无需解密 $decodeSrc")
            return
        }

        val page = extractPageFromUrl(originSrc)
        val cacheFile = File(cacheDir, "decoded_${comicId}_$page.webp")

        // 检查缓存文件是否存在
        if (cacheFile.exists()) {
            decodeSrc = cacheFile.absolutePath
            Log.d("decode image", "缓存存在 $decodeSrc")
            return
        }

        // 加载原始图片
        val request = ImageRequest.Builder(context)
            .data(originSrc)
            .build()

        // TODO SuccessResult 和 ErrorResult
        val result = imageLoader(context).execute(request) as SuccessResult
        val originalBitmap = result.drawable.toBitmap().let {
            if (it.config == Bitmap.Config.HARDWARE) {
                it.copy(Bitmap.Config.ARGB_8888, false)
            } else {
                it
            }
        }

        // 执行解密
        val decodedBitmap = decodeBitmap(originalBitmap, comicId, page)

        // 保存解密后的图片
        saveBitmapAsWebp(decodedBitmap, cacheFile)

        Log.d("decode image", "新建缓存 $decodeSrc")
        decodeSrc = cacheFile.absolutePath
    }

    private fun decodeBitmap(originalBitmap: Bitmap, comicId: Int, page: String): Bitmap {
        val naturalWidth = originalBitmap.width
        val naturalHeight = originalBitmap.height
        val seed = calculateSeed(comicId, page)
        val remainder = naturalHeight % seed

        val decodedBitmap =
            Bitmap.createBitmap(naturalWidth, naturalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(decodedBitmap.asImageBitmap())

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
            val srcSize = IntSize(naturalWidth, sy + height)
            val destOffset = IntOffset(0, dy)
            val destSize = IntSize(naturalWidth, dy + height)

            canvas.drawImageRect(
                originalBitmap.asImageBitmap(),
                srcOffset,
                srcSize,
                destOffset,
                destSize,
                Paint()
            )
        }

        return decodedBitmap
    }

    private fun calculateSeed(comicId: Int, pageStr: String): Int {
        val key = "$comicId$pageStr"
        val keyMd5 = md5(key)
        var charCodeOfLastChar = keyMd5.last().code

        when {
            comicId in LEFT..RIGHT -> charCodeOfLastChar %= 10
            comicId >= RIGHT + 1 -> charCodeOfLastChar %= 8
        }

        return seedMap.getOrNull(charCodeOfLastChar) ?: 10
    }

    private fun extractPageFromUrl(src: String): String {
        return src.substringAfterLast('/').substringBeforeLast('.')
    }

    private fun saveBitmapAsWebp(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, out)
        }
    }
}