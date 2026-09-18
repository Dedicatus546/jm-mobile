package com.par9uet.jm.data.models

import androidx.compose.ui.graphics.ImageBitmap

sealed class ImageResult {
    object Loading : ImageResult()

    data class Success(
        val imageBitmap: ImageBitmap,
        val imageAspectRatio: Float
    ) : ImageResult()

    data class Failure(val reason: String) : ImageResult()
}