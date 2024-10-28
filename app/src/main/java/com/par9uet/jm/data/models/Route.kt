package com.par9uet.jm.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavigationRoute(
    val label: String,
    val value: String,
    val selectIcon: ImageVector,
    val unSelectIcon: ImageVector,
    val show: Boolean = true,
) {
    HOME("首页", "home", Icons.Filled.Home, Icons.Outlined.Home),
    PERSON("个人中心", "person", Icons.Filled.Person, Icons.Outlined.Person),
    LOGIN(
        "登录",
        "login",
        Icons.AutoMirrored.Filled.Login,
        Icons.AutoMirrored.Outlined.Login,
        false
    )
}