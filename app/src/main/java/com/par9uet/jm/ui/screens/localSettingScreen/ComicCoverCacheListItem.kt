package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.provider.LocalCacheManager
import com.par9uet.jm.utils.formatFileSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicCoverCacheListItem() {
    val cacheManager = LocalCacheManager.current
    val comicCoverCacheSize by cacheManager.comicCoverCacheSize.collectAsState()
    LaunchedEffect(Unit) {
        cacheManager.getComicCoverCacheSize()
    }
    SettingGroup("封面") {
        SettingListItem(
            icon = Icons.Default.DataArray,
            iconContentDescription = "图片",
            title = "启用缓存",
        ) {
            Switch(
                checked = true,
                onCheckedChange = {

                }
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.background)
        SettingListItem(
            icon = Icons.Default.Cached,
            iconContentDescription = "缓存",
            title = "缓存上限",
            description = "当前已使用 ${formatFileSize(comicCoverCacheSize)}"
        ) {
            Text("200MB")
        }
    }
}