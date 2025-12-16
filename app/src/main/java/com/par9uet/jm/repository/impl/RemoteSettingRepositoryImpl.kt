package com.par9uet.jm.repository.impl

import com.par9uet.jm.repository.BaseRepository
import com.par9uet.jm.repository.RemoteSettingRepository
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import com.par9uet.jm.retrofit.service.RemoteSettingService

class RemoteSettingRepositoryImpl(
    private val service: RemoteSettingService
) : BaseRepository(), RemoteSettingRepository {
    override suspend fun getRemoteSetting(): NetWorkResult<RemoteSettingResponse> {
        return safeApiCall {
            service.getRemoteSetting()
        }
    }
}