package com.par9uet.jm.store

import com.par9uet.jm.data.models.LocalSetting
import com.par9uet.jm.storage.LocalSettingStorage
import com.par9uet.jm.task.AppInitTask
import com.par9uet.jm.task.AppTaskInfo
import com.par9uet.jm.utils.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocalSettingManager(
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

    fun updatePrefetchCount(prefetchCount: String) {
        _localSettingState.update {
            it.copy(
                prefetchCount = prefetchCount.toInt()
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