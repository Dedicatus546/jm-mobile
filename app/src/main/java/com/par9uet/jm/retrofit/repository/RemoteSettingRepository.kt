package com.par9uet.jm.retrofit.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.par9uet.jm.data.models.RemoteSetting
import com.par9uet.jm.retrofit.Retrofit
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import com.par9uet.jm.retrofit.service.RemoteSettingService

class RemoteSettingRepository(
    retrofit: Retrofit
) : BaseRepository() {

    var remoteSetting by mutableStateOf(RemoteSetting(""))
    private val service = retrofit.createService(RemoteSettingService::class.java)

    suspend fun getRemoteSetting(): NetWorkResult<RemoteSettingResponse> {
        return safeApiCall {
            service.getRemoteSetting()
        }
    }
}