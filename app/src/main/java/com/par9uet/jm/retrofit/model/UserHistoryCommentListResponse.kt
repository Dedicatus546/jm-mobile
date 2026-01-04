package com.par9uet.jm.retrofit.model

import com.par9uet.jm.data.models.Comment
import com.par9uet.jm.utils.translateCommentTime

data class UserHistoryCommentListResponse(
    val list: List<ListItem>,
    val total: Int,
) {
    data class ListItem(
        val AID: String,
        val BID: String,
        val CID: String,
        val UID: String,
        val username: String,
        val nickname: String,
        val likes: String,
        val gender: String,
        val update_at: String,
        val addtime: String,
        val parent_CID: String,
        // 等级相关，这里不写，没啥意义
//        expinfo: {
//        level_name: string
//        level: number
//        nextLevelExp: number
//        exp: string
//        expPercent: number // 100
//        uid: string
//        badges: Array<{
//            content: string
//            name: string
//            id: string
//        }>
//    }
        val name: String,
        val content: String,
        val photo: String,
        val spoiler: String, // 是否剧透 1 和 0
        val replys: List<ListItem>?
    )

    fun toCommentList(): List<Comment> {
        return list.map {
            Comment(
                userId = it.UID.toInt(),
                comicId = it.AID.toInt(),
                id = it.CID.toInt(),
                time = translateCommentTime( it.addtime),
                content = it.content,
                likeCount = it.likes.toInt(),
                username = it.username,
                nickname = it.nickname,
                avatar = it.photo,
                parentId = it.parent_CID.toInt(),
                spoiler = it.spoiler == "1",
                replyCommentList = listOf()
            )
        }
    }
}