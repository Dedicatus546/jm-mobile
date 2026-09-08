package com.par9uet.jm.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LocalSetting(
    val apiList: List<String> = listOf(
        "https://www.cdnhth.club",
        "https://www.cdnmhwscc.vip",
        "https://www.jmapiproxyxxx.vip",
        "https://www.cdnxxx-proxy.xyz",
        "https://www.jmeadpoolcdn.life"
    ),
    val api: String = apiList[0],
    val themeList: List<String> = listOf(
        "auto",
        "light",
        "dark",
    ),
    val theme: String = "auto",
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
    val readMode: String = "scroll", // scroll || page || pageReverse
    val showPageNumber: Boolean = true,
    val noLockScreen: Boolean = false,
    val supportZoom: Boolean = false,

    val downloadPath: String = "",
    val enableComicCoverCache: Boolean = true,
    val comicCoverCacheMaxSize: Long = 1024 * 1024 * 200,
    val enableComicOriginalPicCache: Boolean = true,
    val comicPicOriginalCacheMaxSize: Long = 1024 * 1024 * 200,
    val enableComicDecodePicCache: Boolean = true,
    val comicPicDecodeCacheMaxSize: Long = 1024 * 1024 * 200,
    val comicPicDecodeCompressLevel: String = "lossless", // lossless | lossy
    val comicPicDecodeLossCompressPercent: Float = .7f,

    val logLevel: String = "info"
)