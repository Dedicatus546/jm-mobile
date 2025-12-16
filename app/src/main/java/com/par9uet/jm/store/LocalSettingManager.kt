package com.par9uet.jm.store

import com.par9uet.jm.data.models.LocalSetting
import com.par9uet.jm.storage.LocalSettingStorage
import com.par9uet.jm.task.AppInitTask
import com.par9uet.jm.task.AppTaskInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocalSettingManager(
    private val localSettingStorage: LocalSettingStorage
) : AppInitTask {
    private val _localSettingState = MutableStateFlow<LocalSetting?>(null)
    val localSetting = _localSettingState.asStateFlow()

    private var appTaskInfo = AppTaskInfo(
        taskName = "加载本地 APP 设置",
        sort = 3,
    )

    override suspend fun init() {
        _localSettingState.update {
            localSettingStorage.get()
        }
    }

    override fun getAppTaskInfo(): AppTaskInfo = appTaskInfo
}