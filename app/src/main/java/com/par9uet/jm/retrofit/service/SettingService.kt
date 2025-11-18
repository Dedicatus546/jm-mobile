package com.par9uet.jm.retrofit.service

import com.par9uet.jm.retrofit.model.ResponseWrapper
import com.par9uet.jm.retrofit.model.SettingResponse
import retrofit2.http.GET

interface SettingService {
    @GET("setting")
    suspend fun getSetting(): ResponseWrapper<SettingResponse>
}