package com.par9uet.jm.retrofit.service

import com.par9uet.jm.retrofit.model.CommonResponse
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.UserCollectComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryComicListResponse
import com.par9uet.jm.retrofit.model.UserHistoryCommentListResponse
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserService {

    @POST("login")
    @Multipart
    suspend fun login(
        @Part("username") username: String,
        @Part("password") password: String
    ): CommonResponse<LoginResponse>

    @GET("favorite")
    suspend fun getCollectComicList(
        @Query("page") page: Int,
        @Query("o") order: String,
        @Query("folder_id") folderId: Int = 0
    ): CommonResponse<UserCollectComicListResponse>

    @GET("watch_list")
    suspend fun getHistoryComicList(
        @Query("page") page: Int,
    ): CommonResponse<UserHistoryComicListResponse>

    @GET("forum")
    suspend fun getCommentList(
        @Query("page") page: Int,
        @Query("uid") userId: Int
    ): CommonResponse<UserHistoryCommentListResponse>
}