package com.par9uet.jm.ui.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.par9uet.jm.store.LocalSettingManager

val LocalLocalSettingManager = staticCompositionLocalOf<LocalSettingManager> {
    error("none")
}