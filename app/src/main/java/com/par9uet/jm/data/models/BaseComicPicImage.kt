package com.par9uet.jm.data.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseComicPicImage {
    protected val _imageResult = MutableStateFlow<ImageResult>(ImageResult.Loading)
    val imageResult = _imageResult.asStateFlow()

    abstract suspend fun retry()

    abstract suspend fun load()
}