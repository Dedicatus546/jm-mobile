package com.par9uet.jm.data.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Size
import coil3.toBitmap
import com.par9uet.jm.utils.compressComicPic
import com.par9uet.jm.utils.decodeComicPicBitmap
import com.par9uet.jm.utils.extractPageFromUrl
import com.par9uet.jm.utils.log
import com.par9uet.jm.utils.sha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.buffer
import java.io.ByteArrayOutputStream

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
    val compressLevel: String,
    private val imageLoader: ImageLoader,
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
        val bitmap = getBitmapCache()
        // 检查缓存文件是否存在
        if (bitmap != null) {
            val decodeBitmap = bitmap.asImageBitmap()
            val decodeAspectRatio =
                decodeBitmap.width * 1.0f / decodeBitmap.height
            imageResultState = ImageResultState.Success(decodeBitmap, decodeAspectRatio)
            return
        }

        // 加载原始图片
        val request = ImageRequest.Builder(context)
            .data(originSrc)
            // 这里必须使用原始 size ，不然解密会有问题，出现白线
            .size { Size.ORIGINAL }
            .allowHardware(false)
            .build()

        when (val result = imageLoader.execute(request)) {
            is SuccessResult -> {
                val originalBitmap = result.image.toBitmap()
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
                saveBitmapCache(decodedImageBitmap)
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

    private val cacheKey get() = "$comicId-$page".sha256()

    private fun saveBitmapCache(bitmap: Bitmap) {
        imageLoader.diskCache?.also {
            val outputStream = ByteArrayOutputStream()
            compressComicPic(bitmap, compressLevel, outputStream)
            val bytes = outputStream.toByteArray()
            it.openEditor(cacheKey)?.let { editor ->
                FileSystem.SYSTEM.sink(editor.data).buffer().use { sink ->
                    sink.write(bytes)
                }
            }
        }
    }

    private fun getBitmapCache(): Bitmap? {
        return imageLoader.diskCache?.let {
            it.openSnapshot(cacheKey)?.let { snapshot ->
                val bytes = FileSystem.SYSTEM.source(snapshot.data).buffer().use { source ->
                    source.readByteArray()
                }
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bitmap
            }
        }
    }
}