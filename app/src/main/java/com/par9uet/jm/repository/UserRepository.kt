package com.par9uet.jm.repository

import com.par9uet.jm.data.models.CollectComicOrderFilter
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.retrofit.model.SignInDataResponse
import com.par9uet.jm.retrofit.model.SignInResponse
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryCommentListResponse

interface UserRepository {
    suspend fun login(username: String, password: String): NetworkResult<LoginResponse>
    suspend fun getCollectComicList(
        page: Int = 1,
        order: CollectComicOrderFilter = CollectComicOrderFilter.COLLECT_TIME
    ): NetworkResult<UserCollectComicListResponse>

    suspend fun getHistoryComicList(page: Int = 1): NetworkResult<UserHistoryComicListResponse>
    suspend fun getHistoryCommentList(
        page: Int = 1,
        userId: Int
    ): NetworkResult<UserHistoryCommentListResponse>

    suspend fun getSignData(
        userId: Int,
    ): NetworkResult<SignInDataResponse>

    suspend fun signIn(
        userId: Int,
        dailyId: Int,
    ): NetworkResult<SignInResponse>
}