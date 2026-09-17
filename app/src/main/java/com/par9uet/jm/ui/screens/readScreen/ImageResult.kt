package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.ui.graphics.ImageBitmap

sealed class ImageResult {
    object Loading : ImageResult()

    data class Success(
        val decodeImageBitmap: ImageBitmap,
        val decodeImageAspectRatio: Float
    ) : ImageResult()

    data class Failure(val reason: String) : ImageResult()
}