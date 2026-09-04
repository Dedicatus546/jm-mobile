package com.par9uet.jm.ui.provider

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalTabNavController = staticCompositionLocalOf<NavHostController> {
    error("none")
}