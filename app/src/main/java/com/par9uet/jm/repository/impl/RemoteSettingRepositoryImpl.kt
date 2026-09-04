package com.par9uet.jm.repository.impl

import com.par9uet.jm.repository.BaseRepository
import com.par9uet.jm.repository.RemoteSettingRepository
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import com.par9uet.jm.retrofit.service.RemoteSettingService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class RemoteSettingRepositoryImpl @Inject constructor(
    private val service: RemoteSettingService,
) : BaseRepository(), RemoteSettingRepository {
    override suspend fun getRemoteSetting(): NetworkResult<RemoteSettingResponse> {
        return safeApiCall {
            service.getRemoteSetting()
        }
    }
}