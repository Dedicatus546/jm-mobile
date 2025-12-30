package com.par9uet.jm.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.par9uet.jm.store.LocalSettingManager
import org.koin.compose.getKoin

val LocalExtendedColors = staticCompositionLocalOf<ExtendedColorScheme> {
    error("未提供默认扩展主题变量")
}

object ExtendedTheme {
    val colors: ExtendedColorScheme
        @Composable
        get() = LocalExtendedColors.current
}

@Composable
fun AppTheme(
    localSettingManager: LocalSettingManager = getKoin().get(),
    content: @Composable () -> Unit
) {
    val localSettingState = localSettingManager.localSettingState.collectAsState()
    val theme by remember {
        derivedStateOf {
            localSettingState.value.theme
        }
    }
    val colorScheme = when (theme) {
        "auto" -> {
            val isDark = isSystemInDarkTheme()
            if (isDark) darkScheme else lightScheme
        }

        "light" -> lightScheme
        "dark" -> darkScheme

        else -> lightScheme
    }

    val extendedColorScheme = when (theme) {
        "auto" -> {
            val isDark = isSystemInDarkTheme()
            if (isDark) extendedDark else extendedLight
        }

        "light" -> extendedLight
        "dark" -> extendedDark

        else -> extendedLight
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColorScheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}