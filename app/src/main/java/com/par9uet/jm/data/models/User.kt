package com.par9uet.jm.data.models

data class User(
    val id: Int,
    val username: String,
    val avatar: String ,
    val level: Int,
    val levelName: String,
    val currentLevelExp: Int,
    val nextLevelExp: Int,
    val currentCollectCount: Int,
    val maxCollectCount: Int,
    val jCoin: Int,
)