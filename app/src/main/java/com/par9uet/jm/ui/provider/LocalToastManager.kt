package com.par9uet.jm.ui.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.par9uet.jm.store.ToastManager

val LocalToastManager = staticCompositionLocalOf<ToastManager> {
    error("none")
}