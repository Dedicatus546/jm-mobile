package com.par9uet.jm.retrofit.repository

import com.par9uet.jm.retrofit.Retrofit
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import com.par9uet.jm.retrofit.service.RemoteSettingService

class RemoteSettingRepository(
    retrofit: Retrofit
) : BaseRepository() {
    private val service = retrofit.createService(RemoteSettingService::class.java)

    suspend fun getRemoteSetting(): NetWorkResult<RemoteSettingResponse> {
        return safeApiCall {
            service.getRemoteSetting()
        }
    }
}