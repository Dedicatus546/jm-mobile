package com.par9uet.jm.data.models

enum class ComicSearchOrderFilter(val value: String, val label: String) {
    NEWEST("mr", "最新"),
    MOST_COLLECT_COUNT("mv", "最多收藏"),
    MOST_PIC_COUNT("mp", "最多图片"),
    MOST_LIKE_COUNT("tf", "最多爱心")
}