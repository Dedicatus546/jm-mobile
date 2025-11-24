package com.par9uet.jm.retrofit.service

import com.par9uet.jm.retrofit.model.CollectComicResponse
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.HomeSwiperComicListItemResponse
import com.par9uet.jm.retrofit.model.LikeComicResponse
import com.par9uet.jm.retrofit.model.ResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ComicService {

    @GET("album")
    suspend fun getComicDetail(
        @Query("id") id: Int,
    ): ResponseWrapper<ComicDetailResponse>

    @POST("like")
    @Multipart
    suspend fun likeComic(
        @Part("id") id: Int,
    ): ResponseWrapper<LikeComicResponse>

    @POST("favorite")
    @Multipart
    suspend fun collectComic(
        @Part("aid") id: Int,
    ): ResponseWrapper<CollectComicResponse>

    @GET("promote")
    suspend fun getHomeSwiperComicList(): ResponseWrapper<List<HomeSwiperComicListItemResponse>>
}