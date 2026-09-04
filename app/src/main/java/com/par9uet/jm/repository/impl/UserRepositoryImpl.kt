package com.par9uet.jm.repository.impl

import com.par9uet.jm.data.models.CollectComicOrderFilter
import com.par9uet.jm.repository.BaseRepository
import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.retrofit.model.SignInDataResponse
import com.par9uet.jm.retrofit.model.SignInResponse
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryCommentListResponse
import com.par9uet.jm.retrofit.service.UserService
import com.par9uet.jm.store.InitManager
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val service: UserService
) : BaseRepository(), UserRepository {

    override suspend fun login(username: String, password: String): NetworkResult<LoginResponse> {
        return safeApiCall {
            service.login(username, password)
        }
    }

    override suspend fun getCollectComicList(
        page: Int,
        order: CollectComicOrderFilter
    ): NetworkResult<UserCollectComicListResponse> {
        return safeApiCall {
            service.getCollectComicList(page, order.value)
        }
    }

    override suspend fun getHistoryComicList(page: Int): NetworkResult<UserHistoryComicListResponse> {
        return safeApiCall {
            service.getHistoryComicList(page)
        }
    }

    override suspend fun getHistoryCommentList(
        page: Int,
        userId: Int
    ): NetworkResult<UserHistoryCommentListResponse> {
        return safeApiCall {
            service.getCommentList(page, userId)
        }
    }

    override suspend fun getSignData(userId: Int): NetworkResult<SignInDataResponse> {
        return safeApiCall {
            service.getSignInData(userId)
        }
    }

    override suspend fun signIn(userId: Int, dailyId: Int): NetworkResult<SignInResponse> {
        return safeApiCall {
            service.signIn(userId, dailyId)
        }
    }
}