package com.par9uet.jm.repository

import com.par9uet.jm.data.models.ComicSearchOrderFilter
import com.par9uet.jm.retrofit.model.CollectComicResponse
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.ComicListResponse
import com.par9uet.jm.retrofit.model.HomeSwiperComicListItemResponse
import com.par9uet.jm.retrofit.model.LikeComicResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.WeekRecommendComicResponse
import com.par9uet.jm.retrofit.model.WeekResponse

interface ComicRepository {
    suspend fun getComicDetail(id: Int): NetWorkResult<ComicDetailResponse>
    suspend fun likeComic(id: Int): NetWorkResult<LikeComicResponse>
    suspend fun collectComic(id: Int): NetWorkResult<CollectComicResponse>
    suspend fun unCollectComic(id: Int): NetWorkResult<CollectComicResponse>
    suspend fun getHomeSwiperComicList(): NetWorkResult<List<HomeSwiperComicListItemResponse>>
    suspend fun getComicPicList(id: Int): NetWorkResult<List<String>>
    suspend fun getComicList(
        page: Int,
        order: ComicSearchOrderFilter,
        searchContent: String,
    ): NetWorkResult<ComicListResponse>

    suspend fun getWeekData(): NetWorkResult<WeekResponse>
    suspend fun getWeekRecommendComicList(
        page: Int,
        categoryId: String,
        typeId: String,
    ): NetWorkResult<WeekRecommendComicResponse>
}