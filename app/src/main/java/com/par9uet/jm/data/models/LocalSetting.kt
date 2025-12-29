package com.par9uet.jm.data.models

data class LocalSetting(
    val apiList: List<String> = listOf(
        "https://www.jmapiproxyxxx.vip",
        "https://www.cdnmhwscc.vip",
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
)