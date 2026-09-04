package com.par9uet.jm.ui.provider

import androidx.activity.ComponentActivity
import androidx.compose.runtime.staticCompositionLocalOf

val LocalMainActivity = staticCompositionLocalOf<ComponentActivity> {
    error("none")
}