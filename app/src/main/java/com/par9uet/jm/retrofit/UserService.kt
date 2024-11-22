package com.par9uet.jm.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserService {

    @POST("login")
    @Multipart
    fun login(
        @Part("username") username: String,
        @Part("password") password: String
    ): Call<RespWrapper<LoginResp>>

    @GET("favorite")
    fun getCollectComicList(
        @Query("page") page: Int,
        @Query("o") order: String,
        @Query("folder_id") folderId: Int = 0
    ): Call<RespWrapper<FavoriteResp>>

    @GET("watch_list")
    fun getHistoryComicList(
        @Query("page") page: Int,
    ): Call<RespWrapper<HistoryResp>>
}

data class LoginResp(
    val uid: Int,
    val username: String,
    val email: String,
    val photo: String,
    val coin: String,
    val album_favorites: Int,
    val level_name: String,
    val level: Int,
    val nextLevelExp: Int,
    val exp: Int,
    val expPercent: Double,
    val album_favorites_max: Int,
)

data class FavoriteResp(val total: Int)

data class HistoryResp(val total: Int)