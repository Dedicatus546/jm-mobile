package com.par9uet.jm.repository.impl

import com.par9uet.jm.data.models.CollectComicOrderFilter
import com.par9uet.jm.repository.BaseRepository
import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.SignInDataResponse
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryCommentListResponse
import com.par9uet.jm.retrofit.service.UserService

class UserRepositoryImpl(
    private val service: UserService
) : BaseRepository(), UserRepository {

    override suspend fun login(username: String, password: String): NetWorkResult<LoginResponse> {
        return safeApiCall {
            service.login(username, password)
        }
    }

    override suspend fun getCollectComicList(
        page: Int,
        order: CollectComicOrderFilter
    ): NetWorkResult<UserCollectComicListResponse> {
        return safeApiCall {
            service.getCollectComicList(page, order.value)
        }
    }

    override suspend fun getHistoryComicList(page: Int): NetWorkResult<UserHistoryComicListResponse> {
        return safeApiCall {
            service.getHistoryComicList(page)
        }
    }

    override suspend fun getHistoryCommentList(
        page: Int,
        userId: Int
    ): NetWorkResult<UserHistoryCommentListResponse> {
        return safeApiCall {
            service.getCommentList(page, userId)
        }
    }

    override suspend fun getSignData(userId: Int): NetWorkResult<SignInDataResponse> {
        return safeApiCall {
            service.getSignData(userId)
        }
    }
}