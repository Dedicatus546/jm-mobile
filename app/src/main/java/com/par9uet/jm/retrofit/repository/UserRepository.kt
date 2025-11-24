package com.par9uet.jm.retrofit.repository

import com.par9uet.jm.retrofit.Retrofit
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryCommentListResponse
import com.par9uet.jm.retrofit.service.UserService

class UserRepository(
    retrofit: Retrofit
) : BaseRepository() {
    private val service = retrofit.createService(UserService::class.java)

    suspend fun login(username: String, password: String): NetWorkResult<LoginResponse> {
        return safeApiCall {
            service.login(username, password)
        }
    }

    suspend fun getCollectComicList(
        page: Int = 1,
        order: String = ""
    ): NetWorkResult<UserCollectComicListResponse> {
        return safeApiCall {
            service.getCollectComicList(page, order)
        }
    }

    suspend fun getHistoryComicList(page: Int = 1): NetWorkResult<UserHistoryComicListResponse> {
        return safeApiCall {
            service.getHistoryComicList(page)
        }
    }

    suspend fun getCommentList(
        page: Int = 1,
        userId: Int
    ): NetWorkResult<UserHistoryCommentListResponse> {
        return safeApiCall {
            service.getCommentList(page, userId)
        }
    }
}