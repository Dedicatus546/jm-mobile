package com.par9uet.jm.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.SelectDialog
import com.par9uet.jm.ui.components.SelectOption
import org.koin.compose.getKoin

private sealed class SettingType {
    object Api : SettingType()
    object Theme : SettingType()
    object Shunt : SettingType()
    object PrefetchCount : SettingType()
}

private val themeTextMap = mapOf(
    "auto" to "跟随系统",
    "light" to "日间模式",
    "dark" to "夜间模式",
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSettingScreen(
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    val localSetting by localSettingManager.localSettingState.collectAsState()
    var settingType by remember { mutableStateOf<SettingType>(SettingType.Api) }
    var isOpenSettingSelectDialog by remember { mutableStateOf(false) }
    CommonScaffold(
        title = "设置"
    ) {
        Column {
            ListItem(
                modifier = Modifier.clickable(onClick = {
                    isOpenSettingSelectDialog = true
                    settingType = SettingType.Theme
                }),
                headlineContent = {
                    Text("主题")
                },
                supportingContent = {
                    Text(themeTextMap[localSetting.theme]!!)
                }
            )
            ListItem(
                modifier = Modifier.clickable(onClick = {
                    isOpenSettingSelectDialog = true
                    settingType = SettingType.Api
                }),
                headlineContent = {
                    Text("API 接口")
                },
                supportingContent = {
                    Text(localSetting.api)
                }
            )
            ListItem(
                modifier = Modifier.clickable(onClick = {
                    isOpenSettingSelectDialog = true
                    settingType = SettingType.Shunt
                }),
                headlineContent = {
                    Text("图片线路")
                },
                supportingContent = {
                    Text("线路${localSetting.shunt}")
                }
            )
            ListItem(
                modifier = Modifier.clickable(onClick = {
                    isOpenSettingSelectDialog = true
                    settingType = SettingType.PrefetchCount
                }),
                headlineContent = {
                    Text("图片预载数量")
                },
                supportingContent = {
                    Text("${localSetting.prefetchCount}")
                }
            )
        }
        if (isOpenSettingSelectDialog) {
            val apiSelectOptionList by remember(localSetting.apiList) {
                derivedStateOf {
                    localSetting.apiList.map {
                        // label 去除 https://
                        SelectOption(it.substring(8), it)
                    }
                }
            }
            val themeSelectOptionList by remember(localSetting.themeList) {
                derivedStateOf {
                    localSetting.themeList.map {
                        SelectOption(themeTextMap[it]!!, it)
                    }
                }
            }
            val shuntOptionList by remember(localSetting.shuntList) {
                derivedStateOf {
                    localSetting.shuntList.map {
                        SelectOption("线路$it", it)
                    }
                }
            }
            val prefetchCountOptionList by remember {
                derivedStateOf {
                    listOf(
                        SelectOption("关闭", "0"),
                        SelectOption("预载一张", "1"),
                        SelectOption("预载两张", "2"),
                        SelectOption("预载三张", "3")
                    )
                }
            }
            val title = when (settingType) {
                is SettingType.Api -> "切换接口"
                is SettingType.Theme -> "切换主题"
                is SettingType.Shunt -> "线路选择"
                is SettingType.PrefetchCount -> "图片预载数量"
            }
            val value = when (settingType) {
                is SettingType.Api -> localSetting.api
                is SettingType.Theme -> localSetting.theme
                is SettingType.Shunt -> localSetting.shunt
                is SettingType.PrefetchCount -> "${localSetting.prefetchCount}"
            }
            val selectOptionList = when (settingType) {
                is SettingType.Api -> apiSelectOptionList
                is SettingType.Theme -> themeSelectOptionList
                is SettingType.Shunt -> shuntOptionList
                is SettingType.PrefetchCount -> prefetchCountOptionList
            }
            SelectDialog(
                title = title,
                value = value,
                selectOptionList = selectOptionList,
                onSelect = {
                    when (settingType) {
                        is SettingType.Api -> {
                            localSettingManager.updateApi(it)
                        }

                        is SettingType.Theme -> {
                            localSettingManager.updateTheme(it)
                        }

                        is SettingType.Shunt -> {
                            localSettingManager.updateShunt(it)
                        }

                        is SettingType.PrefetchCount -> {
                            localSettingManager.updatePrefetchCount(it)
                        }
                    }
                    isOpenSettingSelectDialog = false
                },
                onDismissRequest = {
                    isOpenSettingSelectDialog = false
                }
            )
        }
    }
}