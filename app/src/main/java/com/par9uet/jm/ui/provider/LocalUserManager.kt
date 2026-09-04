package com.par9uet.jm.ui.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.par9uet.jm.store.UserManager

val LocalUserManager = staticCompositionLocalOf<UserManager> {
    error("none")
}