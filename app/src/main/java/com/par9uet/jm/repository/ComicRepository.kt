package com.par9uet.jm.repository

import com.par9uet.jm.data.models.ComicSearchOrderFilter
import com.par9uet.jm.retrofit.model.CollectComicResponse
import com.par9uet.jm.retrofit.model.ComicCategoryListResponse
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.ComicFilterListResponse
import com.par9uet.jm.retrofit.model.ComicListResponse
import com.par9uet.jm.retrofit.model.ComicPicListResponse
import com.par9uet.jm.retrofit.model.CommentComicResponse
import com.par9uet.jm.retrofit.model.CommentListResponse
import com.par9uet.jm.retrofit.model.HomeSwiperComicListItemResponse
import com.par9uet.jm.retrofit.model.LikeComicResponse
import com.par9uet.jm.retrofit.model.NetworkResult
import com.par9uet.jm.retrofit.model.WeekRecommendComicResponse
import com.par9uet.jm.retrofit.model.WeekResponse

interface ComicRepository {
    suspend fun getComicDetail(id: Int): NetworkResult<ComicDetailResponse>
    suspend fun likeComic(id: Int): NetworkResult<LikeComicResponse>
    suspend fun collectComic(id: Int): NetworkResult<CollectComicResponse>
    suspend fun unCollectComic(id: Int): NetworkResult<CollectComicResponse>
    suspend fun getHomeSwiperComicList(): NetworkResult<List<HomeSwiperComicListItemResponse>>
    suspend fun getComicPicList(id: Int, shunt: String): NetworkResult<ComicPicListResponse>
    suspend fun getComicList(
        page: Int,
        order: ComicSearchOrderFilter,
        searchContent: String,
    ): NetworkResult<ComicListResponse>

    suspend fun getWeekData(): NetworkResult<WeekResponse>
    suspend fun getWeekRecommendComicList(
        page: Int,
        categoryId: String,
        typeId: String,
    ): NetworkResult<WeekRecommendComicResponse>

    suspend fun getCommentList(
        page: Int,
        comicId: Int,
    ): NetworkResult<CommentListResponse>

    suspend fun comment(
        content: String,
        comicId: Int,
        commentId: Int?
    ): NetworkResult<CommentComicResponse>

    suspend fun getComicFilterList(
        page: Int,
        category: String,
        order: String
    ): NetworkResult<ComicFilterListResponse>

    suspend fun getCategoryList(): NetworkResult<ComicCategoryListResponse>
}