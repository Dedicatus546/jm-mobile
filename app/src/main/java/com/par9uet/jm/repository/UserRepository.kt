package com.par9uet.jm.repository

import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryCommentListResponse

interface UserRepository {
    suspend fun login(username: String, password: String): NetWorkResult<LoginResponse>
    suspend fun getCollectComicList(
        page: Int = 1,
        order: String = ""
    ): NetWorkResult<UserCollectComicListResponse>

    suspend fun getHistoryComicList(page: Int = 1): NetWorkResult<UserHistoryComicListResponse>
    suspend fun getCommentList(
        page: Int = 1,
        userId: Int
    ): NetWorkResult<UserHistoryCommentListResponse>
}