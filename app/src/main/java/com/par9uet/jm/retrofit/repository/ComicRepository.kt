package com.par9uet.jm.retrofit.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.Strictness
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

    suspend fun getComicPicList(id: Int): NetWorkResult<List<String>> {
        return when (val res = safeApiCall {
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
                res as NetWorkResult<List<String>>
            }
        }
    }
}

val g = Gson().newBuilder().setStrictness(Strictness.LENIENT).create()

fun parseHtml(htmlStr: String): List<String> {
    // 正则表达式匹配 result 对象
    val resultRegex = Regex("""const result\s*=\s*(\{[\s\S]*?\});""")
    val resultMatch = resultRegex.find(htmlStr)
    val originPicList = mutableListOf<String>()

    if (resultMatch != null) {
        try {
            val resultJson = resultMatch.groupValues[1]
            val o = g.fromJson(
                resultJson,
                JsonObject::class.java
            )
            val list = o.get("images").asJsonArray
            if (list != null) {
                for (i in 0 until list.size()) {
                    list.get(i).let {
                        if (it.isJsonPrimitive) {
                            originPicList.add(it.asString)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("api", "Error parsing result object: ${e.stackTraceToString()}")
        }
    }

    // 正则表达式匹配 config 对象
    val configRegex = Regex("""const config\s*=\s*(\{[\s\S]*?\});""")
    val configMatch = configRegex.find(htmlStr)
    var imgHost: String? = null
    var jmId: String? = null
    var cache: String? = null

    if (configMatch != null) {
        try {
            val resultJson = configMatch.groupValues[1]
            val o = g.fromJson(
                resultJson,
                JsonObject::class.java
            )
            imgHost = o.get("imghost").asString
            jmId = o.get("jmid").asString
            cache = o.get("cache").asString
        } catch (e: Exception) {
            Log.d("api", "Error parsing config object: ${e.stackTraceToString()}")
        }
    }

    if (originPicList.isEmpty() || imgHost == null || jmId == null || cache == null) {
        Log.d("api", "解析漫画 html 页失败")
        return listOf()
    }

    return originPicList.toList().map { item ->
        "$imgHost/media/photos/$jmId/$item$cache"
    }
}