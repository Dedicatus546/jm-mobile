package com.par9uet.jm.data.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
import com.par9uet.jm.dir.getCommonPicDecodeCacheDir
import com.par9uet.jm.utils.decodeComicPicBitmap
import com.par9uet.jm.utils.extractPageFromUrl
import com.par9uet.jm.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

sealed class ImageResultState {
    object Loading : ImageResultState()
    data class Success(
        val decodeImageBitmap: ImageBitmap,
        val decodeImageAspectRatio: Float
    ) :
        ImageResultState()

    data class Failure(val reason: String) : ImageResultState()
}

class ComicPicImageState(
    val comicId: Int,
    val originSrc: String,
    val scrambleId: Int,
    val speed: String,
    private val picImageLoader: ImageLoader,
) {

    var imageResultState by mutableStateOf<ImageResultState>(ImageResultState.Loading)
    val page = extractPageFromUrl(originSrc)

    suspend fun decode(context: Context) {
        withContext(Dispatchers.Default) {
            imageResultState = ImageResultState.Loading
            decodeImage(context)
        }
    }

    private suspend fun decodeImage(context: Context) {
        val cacheDir = getCommonPicDecodeCacheDir(context, comicId)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val cacheFile = File(cacheDir, "$page.webp")

        // 检查缓存文件是否存在
        if (cacheFile.exists()) {
            val decodeImageBitmap =
                BitmapFactory.decodeFile(cacheFile.absolutePath).asImageBitmap()
            val decodeImageAspectRatio =
                decodeImageBitmap.width * 1.0f / decodeImageBitmap.height
            imageResultState = ImageResultState.Success(decodeImageBitmap, decodeImageAspectRatio)
            return
        }

        // 加载原始图片
        val request = ImageRequest.Builder(context)
            .data(originSrc)
            // 这里必须使用原始 size ，不然解密会有问题，出现白线
            .size { Size.ORIGINAL }
            .allowHardware(false)
            .build()

        when (val result = picImageLoader.execute(request)) {
            is SuccessResult -> {
                val originalBitmap = result.drawable.toBitmap()
                val decodeImageAspectRatio =
                    originalBitmap.width * 1.0f / originalBitmap.height
                val decodedImageBitmap = decodeComicPicBitmap(
                    originSrc,
                    originalBitmap,
                    comicId,
                    scrambleId,
                    speed,
                    page
                )
                saveBitmapAsWebp(decodedImageBitmap, cacheFile)
                imageResultState =
                    ImageResultState.Success(
                        decodedImageBitmap.asImageBitmap(),
                        decodeImageAspectRatio
                    )
            }

            is ErrorResult -> {
                log(result.throwable.stackTraceToString())
                imageResultState = ImageResultState.Failure("网络错误")
            }
        }
    }

    private suspend fun saveBitmapAsWebp(bitmap: Bitmap, file: File) {
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, out)
            }
        }
    }
}