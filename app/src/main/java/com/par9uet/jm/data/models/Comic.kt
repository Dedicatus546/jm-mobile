package com.par9uet.jm.data.models

data class Comic(
    val id: Int,
    val name: String,
    val authorList: List<String> = listOf(),
    val description: String,
    // 阅读次数
    val readCount: Int,
    val likeCount: Int,
    val commentCount: Int,
    val tagList: List<String>,
    // 相关角色
    val roleList: List<String>,
    // 相关作品
    val workList: List<String>,
    // 是否喜爱
    val isLike: Boolean = false,
    // 是否收藏
    val isCollect: Boolean = false,
    // 相关漫画
    val relateComicList: List<Comic> = listOf(),
    // 话数
    val comicChapterList: List<ComicChapter> = listOf(),
    // 价格
    val price: Int,
    // 是否购买
    val isBuy: Boolean = false,
    // 此字段为内部字段
    // 通常情况下，都是使用 id 作为 key 即可
    // 但使用类似 LazyColumn 配合不同参数进行数据更新时，由于 id 一样会导致滚动出现异常
    // 比如使用过滤参数1获取数据，此时结果为 1,2,3,4,5 ，滚动条处于最顶部
    // 然后点击过滤参数2，此时结果 3,4,5,1,2 ，此时按道理滚动条应该位于顶部，但是由于 key 的相同，此时位置会处于 1 ，并且可以向上滚动
    // 为了防止这种情况，我们需要手动生成 key ，比如附带上 order ，或者 searchContent ，来让切换过滤参数时生成不同的唯一的 key
    var comicKey: String = ""
) {
    companion object {
        fun create(
            id: Int,
            name: String,
            authorList: List<String>,
        ): Comic {
            return Comic(
                id = id,
                name = name,
                authorList = authorList,
                description = "",
                readCount = 0,
                likeCount = 0,
                commentCount = 0,
                tagList = listOf(),
                roleList = listOf(),
                workList = listOf(),
                isLike = false,
                isCollect = false,
                relateComicList = listOf(),
                comicChapterList = listOf(),
                price = 0,
                isBuy = false,
            )
        }
    }
}