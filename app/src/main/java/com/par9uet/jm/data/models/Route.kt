package com.par9uet.jm.data.models

import androidx.annotation.DrawableRes
import com.par9uet.jm.R

enum class BottomNavigationRoute(
    val label: String,
    val value: String,
    @DrawableRes
    val selectIcon: Int,
    @DrawableRes
    val unSelectIcon: Int,
    val show: Boolean = true,
) {
    HOME("首页", "home", R.drawable.home_icon, R.drawable.home_icon),
    PERSON("个人中心", "person", R.drawable.person_icon, R.drawable.person_icon),
    LOGIN(
        "登录",
        "login",
        R.drawable.login_icon,
        R.drawable.login_icon,
        false
    )
}