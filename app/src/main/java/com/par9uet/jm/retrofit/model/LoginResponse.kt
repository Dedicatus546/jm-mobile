package com.par9uet.jm.retrofit.model

data class LoginResponse(
    val uid: Int,
    val username: String,
    val email: String,
    val photo: String,
    val coin: String,
    val album_favorites: Int,
    val level_name: String,
    val level: Int,
    val nextLevelExp: Int,
    val exp: Int,
    val expPercent: Double,
    val album_favorites_max: Int,
)