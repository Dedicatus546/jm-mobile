package com.par9uet.jm.retrofit.service

import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.ResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface ComicService {

    @GET("album")
    suspend fun getComicDetail(
        @Query("id") id: Int,
    ): ResponseWrapper<ComicDetailResponse>
}