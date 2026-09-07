package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.par9uet.jm.ui.provider.LocalCacheManager
import com.par9uet.jm.utils.formatFileSize

@Composable
fun ComicDecodePicCacheListItem() {
    val cacheManager = LocalCacheManager.current
    val comicPicDecodeCacheSize by cacheManager.comicPicDecodeCacheSize.collectAsState()
    LaunchedEffect(Unit) {
        cacheManager.getComicPicDecodeCacheSize()
    }
    SettingGroup("解密图片") {
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
            description = "当前已使用 ${formatFileSize(comicPicDecodeCacheSize)}"
        ) {
            Text("200MB")
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.background)
        SettingListItem(
            icon = Icons.Default.Percent,
            iconContentDescription = "百分比",
            title = "解密质量",
            description = "图片从加密转为解密时应用的质量，该选项主要影响缓存占用的大小"
        ) {
            Text("无损")
        }
    }
}