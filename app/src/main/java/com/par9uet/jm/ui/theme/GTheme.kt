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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.par9uet.jm.store.LocalSettingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject


// primary #FF9800
// content tag #EBEEFF
// role tag #F7FAFF
// work tag #FDE7FF

val LocalExtendedColors = staticCompositionLocalOf<ExtendedColorScheme> {
    error("未提供默认扩展主题变量")
}

object ExtendedTheme {
    val colors: ExtendedColorScheme
        @Composable
        get() = LocalExtendedColors.current
}

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    private val localSettingManager: LocalSettingManager,
) : ViewModel() {
    val localSettingState get() = localSettingManager.localSettingState
}

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val appThemeViewModel: AppThemeViewModel = hiltViewModel()
    val localSettingState = appThemeViewModel.localSettingState.collectAsState()
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