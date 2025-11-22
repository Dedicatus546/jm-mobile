package com.par9uet.jm.retrofit.repository

import com.par9uet.jm.retrofit.RetrofitClient
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryComicListResponse
import com.par9uet.jm.retrofit.service.UserService

class UserRepository : BaseRepository() {
    private val service = RetrofitClient.createService(UserService::class.java)

    suspend fun login(username: String, password: String): NetWorkResult<LoginResponse> {
        return safeApiCall {
            service.login(username, password)
        }
    }

    suspend fun getCollectComic(page: Int = 1, order: String = ""): NetWorkResult<UserCollectComicListResponse> {
        return safeApiCall {
            service.getCollectComicList(page, order)
        }
    }

    suspend fun getHistoryComic(page: Int = 1): NetWorkResult<UserHistoryComicListResponse> {
        return safeApiCall {
            service.getHistoryComicList(page)
        }
    }
}