package com.par9uet.jm.data.models

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.par9uet.jm.utils.log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class LocalComicPicImage @AssistedInject constructor(
    @Assisted("comicId") val comicId: Int,
    @Assisted("path") val path: Uri,
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
) : BaseComicPicImage() {

    override suspend fun retry() {
        load()
    }

    override suspend fun load() {
        withContext(Dispatchers.IO) {
            _imageResult.update {
                ImageResult.Loading
            }
            resolveImage(context)
        }
    }

    private suspend fun resolveImage(context: Context) {
        val request = ImageRequest.Builder(context)
            .data(path)
            .allowHardware(false)
            .build()

        when (val result = imageLoader.execute(request)) {
            is SuccessResult -> {
                val originalBitmap = result.image.toBitmap()
                val imageAspectRatio =
                    originalBitmap.width * 1.0f / originalBitmap.height
                _imageResult.update {
                    ImageResult.Success(
                        originalBitmap.asImageBitmap(),
                        imageAspectRatio
                    )
                }

            }

            is ErrorResult -> {
                log(result.throwable.stackTraceToString())
                _imageResult.update {
                    ImageResult.Failure(result.throwable.message ?: "读取图片出错")
                }
            }
        }
    }
}