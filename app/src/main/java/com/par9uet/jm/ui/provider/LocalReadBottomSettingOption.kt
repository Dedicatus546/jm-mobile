package com.par9uet.jm.ui.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.par9uet.jm.data.models.ReadBottomSettingOption

val LocalReadBottomSettingOption = staticCompositionLocalOf<ReadBottomSettingOption> {
    error("none")
}