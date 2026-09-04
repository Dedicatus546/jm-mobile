package com.par9uet.jm.ui.provider

import androidx.compose.runtime.staticCompositionLocalOf
import coil.ImageLoader

val LocalCoverImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("none")
}