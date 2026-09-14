package com.par9uet.jm.database.model.update

data class ResetComicPic(
    val comicId: Int,
    val url: String,
    val isComplete: Boolean = false,
    val md5: String? = null
)