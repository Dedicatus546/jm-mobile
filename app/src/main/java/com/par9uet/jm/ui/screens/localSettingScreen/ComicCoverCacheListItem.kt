package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun ComicCoverCacheListItem() {
    val cacheManager = LocalCacheManager.current
    val localSettingManager = LocalLocalSettingManager.current
    val comicCoverCacheSize by cacheManager.comicCoverCacheSize.collectAsState()
    val localSetting by localSettingManager.localSettingState.collectAsState()
    var cacheSizeExpanded by remember { mutableStateOf(false) }
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
                checked = localSetting.enableComicCoverCache,
                onCheckedChange = {
                    localSettingManager.updateEnableComicCoverCache(it)
                }
            )
        }
        if (localSetting.enableComicCoverCache) {
            HorizontalDivider(color = MaterialTheme.colorScheme.background)
            SettingListItem(
                icon = Icons.Default.Cached,
                iconContentDescription = "缓存",
                title = "缓存上限",
                description = "当前已使用 ${formatFileSize(comicCoverCacheSize)}",
                onClick = {
                    cacheSizeExpanded = true
                }
            ) {
                CacheSizeDropdownMenu(
                    expanded = cacheSizeExpanded,
                    onExpandedChange = {
                        cacheSizeExpanded = it
                    },
                    cacheSize = localSetting.comicCoverCacheMaxSize,
                    onCacheSizeChange = {
                        localSettingManager.updateComicCoverCacheMaxSize(it)
                    }
                )
            }
        }
    }
}