package com.par9uet.jm.repository.impl

import android.util.Log
import com.par9uet.jm.data.models.ComicSearchOrderFilter
import com.par9uet.jm.repository.BaseRepository
import com.par9uet.jm.repository.ComicRepository
import com.par9uet.jm.retrofit.model.CollectComicResponse
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.ComicListResponse
import com.par9uet.jm.retrofit.model.CommentComicResponse
import com.par9uet.jm.retrofit.model.CommentListResponse
import com.par9uet.jm.retrofit.model.HomeSwiperComicListItemResponse
import com.par9uet.jm.retrofit.model.LikeComicResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.model.WeekRecommendComicResponse
import com.par9uet.jm.retrofit.model.WeekResponse
import com.par9uet.jm.retrofit.parseHtml
import com.par9uet.jm.retrofit.service.ComicService

class ComicRepositoryImpl(
    private val service: ComicService
) : BaseRepository(), ComicRepository {
    override suspend fun getComicDetail(id: Int): NetWorkResult<ComicDetailResponse> {
        return safeApiCall {
            service.getComicDetail(id)
        }
    }

    override suspend fun likeComic(id: Int): NetWorkResult<LikeComicResponse> {
        return safeApiCall {
            service.likeComic(id)
        }
    }

    override suspend fun collectComic(id: Int): NetWorkResult<CollectComicResponse> {
        return safeApiCall {
            service.collectComic(id)
        }
    }

    override suspend fun unCollectComic(id: Int): NetWorkResult<CollectComicResponse> {
        return safeApiCall {
            service.collectComic(id)
        }
    }

    override suspend fun getHomeSwiperComicList(): NetWorkResult<List<HomeSwiperComicListItemResponse>> {
        return safeApiCall {
            service.getHomeSwiperComicList()
        }
    }

    override suspend fun getComicPicList(id: Int): NetWorkResult<List<String>> {
        return when (val res = safeStringCall {
            service.getComicPicList(id)
        }) {
            is NetWorkResult.Success<String> -> {
                val htmlStr = res.data
                Log.d("get pic list", "start")
                val res = NetWorkResult.Success(parseHtml(htmlStr))
                Log.d("get pic list", "end")
                res
            }

            else -> {
                NetWorkResult.Error("从 HTML 解析图片列表失败")
            }
        }
    }

    override suspend fun getComicList(
        page: Int,
        order: ComicSearchOrderFilter,
        searchContent: String,
    ): NetWorkResult<ComicListResponse> {
        return safeApiCall {
            service.getComicList(page, order.value, searchContent)
        }
    }

    override suspend fun getWeekData(): NetWorkResult<WeekResponse> {
        return safeApiCall {
            service.getWeekData()
        }
    }

    override suspend fun getWeekRecommendComicList(
        page: Int,
        categoryId: String,
        typeId: String,
    ): NetWorkResult<WeekRecommendComicResponse> {
        return safeApiCall {
            service.getWeekRecommendComicList(
                page,
                categoryId,
                typeId
            )
        }
    }

    override suspend fun getCommentList(
        page: Int,
        comicId: Int
    ): NetWorkResult<CommentListResponse> {
        return safeApiCall {
            service.getCommentList(
                page,
                comicId,
                "manhua"
            )
        }
    }

    override suspend fun comment(
        content: String,
        comicId: Int
    ): NetWorkResult<CommentComicResponse> {
        return safeApiCall {
            service.comment(
                content,
                comicId,
                "1"
            )
        }
    }
}