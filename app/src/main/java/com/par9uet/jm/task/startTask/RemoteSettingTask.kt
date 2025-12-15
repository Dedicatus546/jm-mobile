package com.par9uet.jm.task.startTask

import com.par9uet.jm.data.models.RemoteSetting
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import com.par9uet.jm.retrofit.repository.RemoteSettingRepository
import com.par9uet.jm.task.Task
import com.par9uet.jm.task.TaskResult

class RemoteSettingTask(
    private val remoteSettingRepository: RemoteSettingRepository
) : Task<RemoteSetting> {

    override suspend fun run(): TaskResult<RemoteSetting> {
        return when (val data = remoteSettingRepository.getRemoteSetting()) {
            is NetWorkResult.Error -> {
                TaskResult(isFailure = true)
            }

            is NetWorkResult.Success<RemoteSettingResponse> -> {
                TaskResult(isFailure = false, data = data.data.toRemoteSetting())
            }
        }
    }
}