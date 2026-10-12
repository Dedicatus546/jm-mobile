package com.par9uet.jm.retrofit.service

import retrofit2.http.GET

interface ProxyApiService {
    @GET("jm-mobile-api")
    suspend fun getApiList(): List<String>
}