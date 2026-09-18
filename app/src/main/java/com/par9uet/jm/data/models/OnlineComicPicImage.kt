package com.par9uet.jm.data.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.SuccessResult
import coil3.toBitmap
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.utils.compressComicPic
import com.par9uet.jm.utils.createComicOriginalPicImageRequest
import com.par9uet.jm.utils.decodeComicPicBitmap
import com.par9uet.jm.utils.extractPageFromUrl
import com.par9uet.jm.utils.log
import com.par9uet.jm.utils.sha256
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.buffer
import java.io.ByteArrayOutputStream

class OnlineComicPicImage @AssistedInject constructor(
    @Assisted("comicId") val comicId: Int,
    @Assisted("originSrc") val originSrc: String,
    @Assisted("scrambleId") val scrambleId: Int,
    @Assisted("speed") val speed: String,
    @ApplicationContext private val context: Context,
    private val localSettingManager: LocalSettingManager,
    private val imageLoader: ImageLoader,
) : BaseComicPicImage() {
    val page = extractPageFromUrl(originSrc)
    val compressLevel get() = localSettingManager.localSettingState.value.comicPicDecodeCompressLevel

    override suspend fun retry() {
        load()
    }

    override suspend fun load() {
        withContext(Dispatchers.Default) {
            _imageResult.update {
                ImageResult.Loading
            }
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
            _imageResult.update {
                ImageResult.Success(decodeBitmap, decodeAspectRatio)
            }
            return
        }

        // 加载原始图片
        val request = createComicOriginalPicImageRequest(
            context = context,
            url = originSrc,
            comicId = comicId
        )

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
                _imageResult.update {
                    ImageResult.Success(
                        decodedImageBitmap.asImageBitmap(),
                        decodeImageAspectRatio
                    )
                }

            }

            is ErrorResult -> {
                log(result.throwable.stackTraceToString())
                _imageResult.update {
                    ImageResult.Failure("网络错误")
                }
            }
        }
    }

    private val decodeCacheKey get() = "decode-$comicId-$page".sha256()

    private fun saveBitmapCache(bitmap: Bitmap) {
        imageLoader.diskCache?.also {
            val outputStream = ByteArrayOutputStream()
            compressComicPic(bitmap, compressLevel, outputStream)
            val bytes = outputStream.toByteArray()
            it.openEditor(decodeCacheKey)?.let { editor ->
                FileSystem.SYSTEM.sink(editor.data).buffer().use { sink ->
                    sink.write(bytes)
                }
            }
        }
    }

    private fun getBitmapCache(): Bitmap? {
        return imageLoader.diskCache?.let {
            it.openSnapshot(decodeCacheKey)?.let { snapshot ->
                val bytes = FileSystem.SYSTEM.source(snapshot.data).buffer().use { source ->
                    source.readByteArray()
                }
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bitmap
            }
        }
    }
}