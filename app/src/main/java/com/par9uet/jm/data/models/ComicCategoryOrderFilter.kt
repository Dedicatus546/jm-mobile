package com.par9uet.jm.data.models

enum class ComicCategoryOrderFilter(val value: String, val label: String) {
    NEWEST("", "最新"),
    MOST_LIKE_COUNT("tf", "最多爱心"),
    RANK("mv", "总排行"),
    MONTH_RANK("mv_m", "月排行"),
    WEEK_RANK("mv_w", "周排行"),
    DAY_RANK("mv_t", "日排行"),
}