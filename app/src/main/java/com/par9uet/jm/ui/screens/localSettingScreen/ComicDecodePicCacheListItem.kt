package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.components.SettingGroup
import com.par9uet.jm.ui.components.SettingListItem
import com.par9uet.jm.ui.provider.LocalCacheManager
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.utils.formatFileSize

private val compressTextMap = mapOf(
    "lossless" to "无损",
    "loss" to "有损",
)

@Composable
fun ComicDecodePicCacheListItem() {
    val cacheManager = LocalCacheManager.current
    val localSettingManager = LocalLocalSettingManager.current
    val localSetting by localSettingManager.localSettingState.collectAsState()
    val comicPicDecodeCacheSize by cacheManager.comicPicDecodeCacheSize.collectAsState()
    var compressLevelExpanded by remember { mutableStateOf(false) }
    var cacheSizeExpanded by remember { mutableStateOf(false) }
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
                checked = localSetting.enableComicDecodePicCache,
                onCheckedChange = {
                    localSettingManager.updateEnableComicDecodePicCache(it)
                }
            )
        }
        if (localSetting.enableComicDecodePicCache) {
            HorizontalDivider(color = MaterialTheme.colorScheme.background)
            SettingListItem(
                icon = Icons.Default.Cached,
                iconContentDescription = "缓存",
                title = "缓存上限",
                description = "当前已使用 ${formatFileSize(comicPicDecodeCacheSize)}",
                onClick = {
                    cacheSizeExpanded = true
                }
            ) {
                CacheSizeDropdownMenu(
                    expanded = cacheSizeExpanded,
                    onExpandedChange = {
                        cacheSizeExpanded = it
                    },
                    cacheSize = localSetting.comicPicDecodeCacheMaxSize,
                    onCacheSizeChange = {
                        localSettingManager.updateComicPicDecodeCacheMaxSize(it)
                    }
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.background)
            SettingListItem(
                icon = Icons.Default.Percent,
                iconContentDescription = "解密质量",
                title = "解密质量",
                description = "图片从加密转为解密时应用的质量，该选项主要影响缓存占用的大小",
                onClick = {
                    compressLevelExpanded = true
                }
            ) {
                ExposedDropdownMenuBox(
                    expanded = compressLevelExpanded,
                    onExpandedChange = {
                        compressLevelExpanded = it
                    }
                ) {
                    Text(compressTextMap[localSetting.comicPicDecodeCompressLevel]!!)
                    ExposedDropdownMenu(
                        modifier = Modifier.width(200.dp),
                        expanded = compressLevelExpanded,
                        onDismissRequest = { compressLevelExpanded = false },
                    ) {
                        compressTextMap.forEach { (level, label) ->
                            DropdownMenuItem(
                                trailingIcon = {
                                    if (localSetting.comicPicDecodeCompressLevel == level) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "选中$label"
                                        )
                                    }
                                },
                                text = {
                                    Text(label)
                                },
                                onClick = {
                                    localSettingManager.updateComicPicDecodeCompressLevel(level)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}