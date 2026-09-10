package com.par9uet.jm.store

import coil3.ImageLoader
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class CacheManager @Inject constructor(
    private val imageLoader: ImageLoader
) {

    val cacheSize get() = imageLoader.diskCache?.size ?: 0

    fun clearCache() {
        imageLoader.diskCache?.clear()
    }
}