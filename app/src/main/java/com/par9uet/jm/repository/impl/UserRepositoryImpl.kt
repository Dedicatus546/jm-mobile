package com.par9uet.jm.repository.impl

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.par9uet.jm.repository.BaseRepository
import com.par9uet.jm.repository.UserRepository
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryCommentListResponse
import com.par9uet.jm.retrofit.service.UserService
import com.par9uet.jm.utils.createUser

class UserRepositoryImpl(
    private val service: UserService
) : BaseRepository(), UserRepository {
    var user by mutableStateOf(createUser())
    var isAutoLogin by mutableStateOf(false)
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    override suspend fun login(username: String, password: String): NetWorkResult<LoginResponse> {
        return safeApiCall {
            service.login(username, password)
        }
    }

    override suspend fun getCollectComicList(
        page: Int,
        order: String
    ): NetWorkResult<UserCollectComicListResponse> {
        return safeApiCall {
            service.getCollectComicList(page, order)
        }
    }

    override suspend fun getHistoryComicList(page: Int): NetWorkResult<UserHistoryComicListResponse> {
        return safeApiCall {
            service.getHistoryComicList(page)
        }
    }

    override suspend fun getCommentList(
        page: Int,
        userId: Int
    ): NetWorkResult<UserHistoryCommentListResponse> {
        return safeApiCall {
            service.getCommentList(page, userId)
        }
    }
}