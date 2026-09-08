package com.par9uet.jm.store

import com.par9uet.jm.data.models.LocalSetting
import com.par9uet.jm.storage.LocalSettingStorage
import com.par9uet.jm.task.AppInitTask
import com.par9uet.jm.task.AppTaskInfo
import com.par9uet.jm.utils.log
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class LocalSettingManager @Inject constructor(
    private val localSettingStorage: LocalSettingStorage
) : AppInitTask {
    private val _localSettingState = MutableStateFlow(LocalSetting())
    val localSettingState = _localSettingState.asStateFlow()

    fun updateApi(api: String) {
        _localSettingState.update {
            it.copy(
                api = api
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateTheme(theme: String) {
        _localSettingState.update {
            it.copy(
                theme = theme
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateShunt(shunt: String) {
        _localSettingState.update {
            it.copy(
                shunt = shunt
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updatePrefetchCount(prefetchCount: Int) {
        _localSettingState.update {
            it.copy(
                prefetchCount = prefetchCount
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateReadMode(readMode: String) {
        _localSettingState.update {
            it.copy(
                readMode = readMode
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun closeShowComicScrollReadTip() {
        _localSettingState.update {
            it.copy(
                showComicScrollReadTip = false
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun closeShowComicPageReadTip() {
        _localSettingState.update {
            it.copy(
                showComicPageReadTip = false
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateBrightness(brightness: Float) {
        _localSettingState.update {
            it.copy(
                brightness = brightness
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateBrightnessFollowSystem(brightnessFollowSystem: Boolean) {
        _localSettingState.update {
            it.copy(
                brightnessFollowSystem = brightnessFollowSystem
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateShowPageNumber(showPageNumber: Boolean) {
        _localSettingState.update {
            it.copy(
                showPageNumber = showPageNumber
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateNoLockScreen(noLockScreen: Boolean) {
        _localSettingState.update {
            it.copy(
                noLockScreen = noLockScreen
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateSupportZoom(supportZoom: Boolean) {
        _localSettingState.update {
            it.copy(
                supportZoom = supportZoom
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateEnableComicDecodePicCache(enable: Boolean) {
        _localSettingState.update {
            it.copy(
                enableComicDecodePicCache = enable
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateComicPicDecodeCompressLevel(level: String) {
        _localSettingState.update {
            it.copy(
                comicPicDecodeCompressLevel = level
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateComicPicDecodeCacheMaxSize(cacheSize: Long) {
        _localSettingState.update {
            it.copy(
                comicPicDecodeCacheMaxSize = cacheSize
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateEnableComicOriginalPicCache(enable: Boolean) {
        _localSettingState.update {
            it.copy(
                enableComicOriginalPicCache = enable
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateComicPicOriginalCacheMaxSize(cacheSize: Long) {
        _localSettingState.update {
            it.copy(
                comicPicOriginalCacheMaxSize = cacheSize
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateEnableComicCoverCache(enable: Boolean) {
        _localSettingState.update {
            it.copy(
                enableComicCoverCache = enable
            )
        }
        localSettingStorage.set(_localSettingState.value)
    }

    fun updateComicCoverCacheMaxSize(cacheSize: Long) {
        _localSettingState.update {
            it.copy(
                comicCoverCacheMaxSize = cacheSize
            )
        }
    }

    private var appTaskInfo = AppTaskInfo(
        taskName = "加载本地 APP 设置",
        sort = 3,
    )

    override suspend fun init() {
        log("本地应用设置开始初始化")
        log("加载本地应用设置")
        _localSettingState.update {
            localSettingStorage.get()
        }
        log("已加载本地应用设置")
        log("本地应用设置初始化结束")
    }

    override fun getAppTaskInfo(): AppTaskInfo = appTaskInfo
}