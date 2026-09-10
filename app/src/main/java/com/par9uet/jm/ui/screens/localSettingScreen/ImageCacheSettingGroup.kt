package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.par9uet.jm.ui.components.SettingGroup
import com.par9uet.jm.ui.components.SettingListItem
import com.par9uet.jm.ui.provider.LocalCacheManager
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.utils.formatFileSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCacheSettingGroup() {
    val cacheManager = LocalCacheManager.current
    val localSettingManager = LocalLocalSettingManager.current
    val localSetting by localSettingManager.localSettingState.collectAsState()
    var cacheSizeExpanded by remember { mutableStateOf(false) }

    SettingGroup(title = "图片缓存") {
        SettingListItem(
            icon = Icons.Default.DataArray,
            iconContentDescription = "图片",
            title = "启用缓存",
        ) {
            Switch(
                checked = localSetting.enableImageCache,
                onCheckedChange = {
                    localSettingManager.updateEnableImageCache(it)
                }
            )
        }
        if (localSetting.enableImageCache) {
            HorizontalDivider(color = MaterialTheme.colorScheme.background)
            SettingListItem(
                icon = Icons.Default.Cached,
                iconContentDescription = "缓存",
                title = "缓存上限",
                description = "当前已使用 ${formatFileSize(cacheManager.cacheSize)}",
                onClick = {
                    cacheSizeExpanded = true
                }
            ) {
                CacheSizeDropdownMenu(
                    expanded = cacheSizeExpanded,
                    onExpandedChange = {
                        cacheSizeExpanded = it
                    },
                    cacheSize = localSetting.imageCacheMaxSize,
                    onCacheSizeChange = {
                        localSettingManager.updateImageCacheMaxSize(it)
                    }
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.background)
            SettingListItem(
                icon = Icons.Default.Remove,
                iconContentDescription = "清除缓存",
                title = "清除缓存",
                onClick = {
                    cacheManager.clearComicCoverCache()
                }
            )
        }
    }
}