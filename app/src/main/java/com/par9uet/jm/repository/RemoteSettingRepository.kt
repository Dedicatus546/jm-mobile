package com.par9uet.jm.repository

import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse

interface RemoteSettingRepository {
    suspend fun getRemoteSetting(): NetworkResult<RemoteSettingResponse>
}