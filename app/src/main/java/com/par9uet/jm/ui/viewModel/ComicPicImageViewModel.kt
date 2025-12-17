package com.par9uet.jm.ui.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
import com.par9uet.jm.cache.getCommonPicDecodeCacheDir
import com.par9uet.jm.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class ComicPicImageViewModel(
    private val picImageLoader: ImageLoader
) : ViewModel() {
    private val comicPicImageStateMap: MutableMap<String, ComicPicImageState> = mutableMapOf()

    fun getComicPicImageState(comicId: Int, src: String): ComicPicImageState {
        val key = "${comicId}_$src"
        var comicPicImageState: ComicPicImageState? = comicPicImageStateMap[key]
        if (comicPicImageState == null) {
            comicPicImageState = ComicPicImageState(comicId, src, picImageLoader)
            comicPicImageStateMap[key] = comicPicImageState
        }
        return comicPicImageState
    }

    fun decode(comicId: Int, src: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = getComicPicImageState(comicId, src)
            when (state.imageResult) {
                is ImageResult.Success -> {
                    Log.d(
                        "pic image",
                        "已解密，跳过 src: $src comicId: $comicId"
                    )
                }

                is ImageResult.Pending -> {
                    Log.d("pic image", "开始解密图片 src: $src comicId: $comicId")
                    state.decode(context)
                }

                else -> {}
            }
        }
    }
}

private val seedMap = listOf(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)
private const val LEFT = 268850
private const val RIGHT = 421925

sealed class ImageResult {
    class Pending : ImageResult()
    class Loading : ImageResult()
    data class Success(
        val decodeImageBitmap: ImageBitmap,
        val decodeImageAspectRatio: Float
    ) :
        ImageResult()

    data class Failure(val reason: String) : ImageResult()
}

class ComicPicImageState(
    private val comicId: Int,
    private val originSrc: String,
    private val picImageLoader: ImageLoader
) {
    var imageResult by mutableStateOf<ImageResult>(ImageResult.Pending())

    suspend fun decode(context: Context) {
        imageResult = ImageResult.Loading()
        decodeImage(context)
    }

    private suspend fun decodeImage(context: Context) {
        val cacheDir = getCommonPicDecodeCacheDir(context, comicId)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val page = extractPageFromUrl()
        val cacheFile = File(cacheDir, "$page.webp")

        // 检查缓存文件是否存在
        if (cacheFile.exists()) {
            val decodeImageBitmap =
                BitmapFactory.decodeFile(cacheFile.absolutePath).asImageBitmap()
            val decodeImageAspectRatio = decodeImageBitmap.width * 1.0f / decodeImageBitmap.height
            imageResult = ImageResult.Success(decodeImageBitmap, decodeImageAspectRatio)
            return
        }

        // 加载原始图片
        val request = ImageRequest.Builder(context)
            .data(originSrc)
            // 这里必须使用原始 size ，不然解密会有问题，出现白线
            .size { Size.ORIGINAL }
            .build()

        when (val result = picImageLoader.execute(request)) {
            is SuccessResult -> {
                val originalBitmap = result.drawable.toBitmap().let {
                    if (it.config == Bitmap.Config.HARDWARE) {
                        it.copy(Bitmap.Config.ARGB_8888, false)
                    } else {
                        it
                    }
                }
                val originalImageBitmap = originalBitmap.asImageBitmap()
                val decodeImageAspectRatio =
                    originalImageBitmap.width * 1.0f / originalImageBitmap.height
                var decodedImageBitmap = originalImageBitmap
                if (comicId <= LEFT) {
                    saveBitmapAsWebp(originalBitmap, cacheFile)
                } else {
                    val decodedBitmap = decodeBitmap(originalBitmap, page)
                    saveBitmapAsWebp(decodedBitmap, cacheFile)
                    decodedImageBitmap = decodedBitmap.asImageBitmap()
                }
                imageResult = ImageResult.Success(decodedImageBitmap, decodeImageAspectRatio)
            }

            is ErrorResult -> {
                Log.d("comic pic", result.throwable.stackTraceToString())
                imageResult = ImageResult.Failure("网络错误")
            }
        }
    }

    private fun decodeBitmap(originalBitmap: Bitmap, page: String): Bitmap {
        val naturalWidth = originalBitmap.width
        val naturalHeight = originalBitmap.height
        val seed = calculateSeed(comicId, page)
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
//            Log.d("decode image", "0 $sy $naturalWidth $height 0 $dy $naturalWidth $height")

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

        when {
            comicId in LEFT..RIGHT -> charCodeOfLastChar %= 10
            comicId >= RIGHT + 1 -> charCodeOfLastChar %= 8
        }

        return seedMap.getOrNull(charCodeOfLastChar) ?: 10
    }

    private fun extractPageFromUrl(): String {
        return originSrc.substringAfterLast('/').substringBeforeLast('.')
    }

    private fun saveBitmapAsWebp(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, out)
        }
    }
}