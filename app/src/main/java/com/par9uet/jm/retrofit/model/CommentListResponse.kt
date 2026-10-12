package com.par9uet.jm.retrofit.model

import com.par9uet.jm.data.models.Comment
import com.par9uet.jm.utils.formatCommentTime
import kotlinx.serialization.Serializable

@Serializable
data class CommentListResponse(
    val list: List<ListItem>,
    val total: String,
) {
    @Serializable
    data class ListItem(
        val AID: String? = null,
        val BID: String? = null,
        val CID: String,
        val UID: String,
        val username: String,
        val nickname: String,
        val likes: String,
        // val gender: String,
        // val update_at: String,
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
//         val name: String,
        val content: String,
        val photo: String,
        val spoiler: String, // 是否剧透 1 和 0
        val replys: List<ListItem>? = null
    ) {
        fun toComment(): Comment = Comment(
            userId = UID.toInt(),
            comicId = AID?.toInt() ?: 0,
            id = CID.toInt(),
            time = formatCommentTime(addtime),
            content = content,
            likeCount = likes.toInt(),
            username = username,
            nickname = nickname,
            avatar = photo,
            parentId = parent_CID.toInt(),
            spoiler = spoiler == "1",
            replyCommentList = replys?.map { it.toComment() } ?: listOf()
        )
    }

    fun toCommentList(): List<Comment> {
        return list.map {
            it.toComment()
        }
    }
}