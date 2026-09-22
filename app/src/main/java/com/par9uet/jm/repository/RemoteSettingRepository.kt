package com.par9uet.jm.repository

import com.par9uet.jm.repository.RemoteSettingRepository
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import com.par9uet.jm.retrofit.service.RemoteSettingService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class RemoteSettingRepository @Inject constructor(
    private val service: RemoteSettingService,
) : BaseRepository() {
    suspend fun getRemoteSetting(): NetworkResult<RemoteSettingResponse> {
        return safeApiCall {
            service.getRemoteSetting()
        }
    }
}