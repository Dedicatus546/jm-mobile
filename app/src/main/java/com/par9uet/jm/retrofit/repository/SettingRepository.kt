package com.par9uet.jm.retrofit.repository

import com.par9uet.jm.retrofit.RetrofitClient
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.SettingResponse
import com.par9uet.jm.retrofit.service.SettingService

class SettingRepository : BaseRepository() {
    private val service = RetrofitClient.createService(SettingService::class.java)

    suspend fun getSetting(): NetWorkResult<SettingResponse> {
        return safeApiCall {
            service.getSetting()
        }
    }
}