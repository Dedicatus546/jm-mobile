package com.par9uet.jm.retrofit.repository

import com.par9uet.jm.retrofit.Retrofit
import com.par9uet.jm.retrofit.model.CollectComicResponse
import com.par9uet.jm.retrofit.model.ComicDetailResponse
import com.par9uet.jm.retrofit.model.HomeSwiperComicListItemResponse
import com.par9uet.jm.retrofit.model.LikeComicResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.service.ComicService

class ComicRepository(
    retrofit: Retrofit
) : BaseRepository() {
    private val service = retrofit.createService(ComicService::class.java)

    suspend fun getComicDetail(id: Int): NetWorkResult<ComicDetailResponse> {
        return safeApiCall {
            service.getComicDetail(id)
        }
    }

    suspend fun likeComic(id: Int): NetWorkResult<LikeComicResponse> {
        return safeApiCall {
            service.likeComic(id)
        }
    }

    suspend fun collectComic(id: Int): NetWorkResult<CollectComicResponse> {
        return safeApiCall {
            service.collectComic(id)
        }
    }

    suspend fun unCollectComic(id: Int): NetWorkResult<CollectComicResponse> {
        return safeApiCall {
            service.collectComic(id)
        }
    }

    suspend fun getHomeSwiperComicList(): NetWorkResult<List<HomeSwiperComicListItemResponse>> {
        return safeApiCall {
            service.getHomeSwiperComicList()
        }
    }
}