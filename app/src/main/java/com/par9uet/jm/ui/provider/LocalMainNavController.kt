package com.par9uet.jm.ui.provider

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalMainNavController = staticCompositionLocalOf<NavHostController> {
    error("none")
}