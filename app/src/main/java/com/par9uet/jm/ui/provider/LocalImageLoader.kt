package com.par9uet.jm.ui.provider

import androidx.compose.runtime.staticCompositionLocalOf
import coil3.ImageLoader

val LocalImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("none")
}