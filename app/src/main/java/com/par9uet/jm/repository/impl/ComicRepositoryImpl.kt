package com.par9uet.jm.repository.impl

import com.par9uet.jm.data.models.ComicSearchOrderFilter
import com.par9uet.jm.repository.BaseRepository
import com.par9uet.jm.repository.ComicRepository
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
import com.par9uet.jm.retrofit.parseHtml
import com.par9uet.jm.retrofit.parseRange
import com.par9uet.jm.retrofit.parseSpeed
import com.par9uet.jm.retrofit.service.ComicService
import com.par9uet.jm.utils.log
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ComicRepositoryImpl @Inject constructor(
    private val service: ComicService,
) : BaseRepository(), ComicRepository {
    override suspend fun getComicDetail(id: Int): NetworkResult<ComicDetailResponse> {
        return safeApiCall {
            service.getComicDetail(id)
        }
    }

    override suspend fun likeComic(id: Int): NetworkResult<LikeComicResponse> {
        return safeApiCall {
            service.likeComic(id)
        }
    }

    override suspend fun collectComic(id: Int): NetworkResult<CollectComicResponse> {
        return safeApiCall {
            service.collectComic(id)
        }
    }

    override suspend fun unCollectComic(id: Int): NetworkResult<CollectComicResponse> {
        return safeApiCall {
            service.collectComic(id)
        }
    }

    override suspend fun getHomeSwiperComicList(): NetworkResult<List<HomeSwiperComicListItemResponse>> {
        return safeApiCall {
            service.getHomeSwiperComicList()
        }
    }

    override suspend fun getComicPicList(
        id: Int,
        shunt: String
    ): NetworkResult<ComicPicListResponse> {
        return when (val res = safeStringCall {
            service.getComicPicList(id, shunt)
        }) {
            is NetworkResult.Success<String> -> {
                val htmlStr = res.data
                val pair = parseRange(htmlStr)
                val r = ComicPicListResponse(
                    list = parseHtml(htmlStr),
                    __aId = pair.first,
                    __scrambleId = pair.second,
                    __speed = parseSpeed(htmlStr)
                )
                log("r = $r")
                NetworkResult.Success(
                    r
                )
            }

            else -> {
                NetworkResult.Error("从 HTML 解析图片列表失败")
            }
        }
    }

    override suspend fun getComicList(
        page: Int,
        order: ComicSearchOrderFilter,
        searchContent: String,
    ): NetworkResult<ComicListResponse> {
        return safeApiCall {
            service.getComicList(page, order.value, searchContent)
        }
    }

    override suspend fun getWeekData(): NetworkResult<WeekResponse> {
        return safeApiCall {
            service.getWeekData()
        }
    }

    override suspend fun getWeekRecommendComicList(
        page: Int,
        categoryId: String,
        typeId: String,
    ): NetworkResult<WeekRecommendComicResponse> {
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
    ): NetworkResult<CommentListResponse> {
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
        comicId: Int,
        commentId: Int?
    ): NetworkResult<CommentComicResponse> {
        return safeApiCall {
            service.comment(
                content,
                comicId,
                "1",
                commentId,
            )
        }
    }

    override suspend fun getComicFilterList(
        page: Int,
        category: String,
        order: String
    ): NetworkResult<ComicFilterListResponse> {
        return safeApiCall {
            service.getComicFilterList(
                page = page,
                category = category,
                order = order,
            )
        }
    }

    override suspend fun getCategoryList(): NetworkResult<ComicCategoryListResponse> {
        return safeApiCall {
            service.getCategoryList()
        }
    }
}