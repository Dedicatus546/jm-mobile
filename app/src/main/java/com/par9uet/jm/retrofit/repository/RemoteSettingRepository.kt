package com.par9uet.jm.retrofit.repository

import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.RemoteSettingResponse

interface RemoteSettingRepository {
    suspend fun getRemoteSetting(): NetWorkResult<RemoteSettingResponse>
}