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
        }
        if (isOpenSettingSelectDialog) {
            val title = when (settingType) {
                is SettingType.Api -> "API 接口"
                is SettingType.Theme -> "主题"
            }
            val value = when (settingType) {
                is SettingType.Api -> localSetting.api
                is SettingType.Theme -> localSetting.theme
            }
            val selectOptionList = when (settingType) {
                is SettingType.Api -> apiSelectOptionList
                is SettingType.Theme -> themeSelectOptionList
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