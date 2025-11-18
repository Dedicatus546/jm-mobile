package com.par9uet.jm.retrofit.repository

import com.par9uet.jm.retrofit.RetrofitClient
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.service.ComicService

class ComicRepository : BaseRepository() {
    private val service = RetrofitClient.createService(ComicService::class.java)

    suspend fun getComicDetail(id: Int): NetWorkResult<ComicDetailResponse> {
        return safeApiCall {
            service.getComicDetail(id)
        }
    }
}