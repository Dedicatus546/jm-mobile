package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.Theme
import com.par9uet.jm.ui.components.SettingListItem
import com.par9uet.jm.ui.provider.LocalLocalSettingManager

private val themeMetaDataMap = mapOf(
    Theme.AUTO to Pair(Icons.Default.AutoMode, "跟随系统"),
    Theme.LIGHT to Pair(Icons.Default.LightMode, "日间模式"),
    Theme.DARK to Pair(Icons.Default.DarkMode, "夜间模式"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingListItem() {
    val localSettingManager = LocalLocalSettingManager.current
    val localSetting by localSettingManager.localSettingState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    SettingListItem(
        icon = Icons.Default.ColorLens,
        iconContentDescription = "主题",
        title = "主题",
        onClick = {
            expanded = true
        }
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = it
            }
        ) {
            Text(themeMetaDataMap[localSetting.theme]!!.second)
            ExposedDropdownMenu(
                modifier = Modifier.width(200.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                themeMetaDataMap.forEach { (theme, metaData) ->
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = metaData.first,
                                contentDescription = metaData.second
                            )
                        },
                        trailingIcon = {
                            if (localSetting.theme == theme) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "选中${metaData.second}"
                                )
                            }
                        },
                        text = {
                            Text(metaData.second)
                        },
                        onClick = {
                            localSettingManager.updateTheme(theme)
                        }
                    )
                }
            }
        }
    }
}