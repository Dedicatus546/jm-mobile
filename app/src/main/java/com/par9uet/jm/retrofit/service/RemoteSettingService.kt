package com.par9uet.jm.retrofit.service

import com.par9uet.jm.retrofit.model.CommonResponse
import com.par9uet.jm.retrofit.model.RemoteSettingResponse
import retrofit2.http.GET

interface RemoteSettingService {
    @GET("setting")
    suspend fun getRemoteSetting(): CommonResponse<RemoteSettingResponse>
}