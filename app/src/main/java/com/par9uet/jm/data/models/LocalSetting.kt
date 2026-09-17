package com.par9uet.jm.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LocalSetting(
    val apiList: List<String> = listOf(
        "https://www.cdngwc.cc"
    ),
    val api: String = apiList[0],
    val theme: Theme = Theme.AUTO,
    // 阅读页预先加载的图片张数
    val prefetchCount: Int = 3,
    val showComicScrollReadTip: Boolean = true,
    val showComicPageReadTip: Boolean = true,
    val shunt: String = "1",
    val shuntList: List<String> = listOf(
        "1",
        "2",
        "3",
        "4",
    ),
    val brightnessFollowSystem: Boolean = true,
    val brightness: Float = .5f,
    val readMode: ReadMode = ReadMode.SCROLL,
    val showPageNumber: Boolean = true,
    val noLockScreen: Boolean = false,
    val supportZoom: Boolean = false,
    val enableImageCache: Boolean = true,
    val imageCacheMaxSize: Long = 1024 * 1024 * 200,
    val comicPicDecodeCompressLevel: ComicPicDecodeCompressLevel = ComicPicDecodeCompressLevel.LOSS_LESS,
    // val downloadPath: String = "",
    // val logLevel: String = "info"
)