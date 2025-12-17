package com.par9uet.jm.data.models

data class UserLoginInfo(
    val isAutoLogin: Boolean = false,
    val username: String = "",
    val password: String = ""
)