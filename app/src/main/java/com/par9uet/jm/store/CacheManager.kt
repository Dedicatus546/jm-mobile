package com.par9uet.jm.store

import androidx.compose.runtime.MutableIntState
import coil3.ImageLoader
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class CacheManager @Inject constructor(
    private val imageLoader: ImageLoader,
    private val toastManager: ToastManager
) {
    val cacheSize = MutableStateFlow(imageLoader.diskCache?.size ?: 0)

    fun refreshCacheSize() {
        cacheSize.update {
            imageLoader.diskCache?.size ?: 0
        }
    }

    fun clearCache() {
        imageLoader.diskCache?.clear()
        toastManager.show("清除成功")
        cacheSize.update {
            0
        }
    }
}