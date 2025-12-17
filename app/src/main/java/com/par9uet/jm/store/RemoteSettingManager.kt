package com.par9uet.jm.store

import com.par9uet.jm.data.models.RemoteSetting
import com.par9uet.jm.repository.RemoteSettingRepository
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import com.par9uet.jm.task.AppInitTask
import com.par9uet.jm.task.AppTaskInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RemoteSettingManager(
    private val remoteSettingRepository: RemoteSettingRepository
) : AppInitTask {
    private val _remoteSettingState = MutableStateFlow<RemoteSetting?>(null)
    val remoteSettingState = _remoteSettingState.asStateFlow()

    private var appTaskInfo = AppTaskInfo(
        taskName = "加载 app 远端应用数据",
        sort = 1,
    )

    private suspend fun getRemoteSetting() {
        when (val data = remoteSettingRepository.getRemoteSetting()) {
            is NetWorkResult.Error -> {
                appTaskInfo = appTaskInfo.copy(
                    isError = true,
                    errorMsg = data.message
                )
            }

            is NetWorkResult.Success<RemoteSettingResponse> -> {
                _remoteSettingState.update {
                    data.data.toRemoteSetting()
                }
            }
        }
    }

    override suspend fun init() {
        getRemoteSetting()
    }

    override fun getAppTaskInfo(): AppTaskInfo = appTaskInfo
}