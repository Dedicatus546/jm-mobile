package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.store.LocalSettingManager
import org.koin.compose.getKoin

private val themeTextMap = mapOf(
    "auto" to "跟随系统",
    "light" to "日间模式",
    "dark" to "夜间模式",
)

private val themeIconMap = mapOf(
    "auto" to Icons.Default.AutoMode,
    "light" to Icons.Default.LightMode,
    "dark" to Icons.Default.DarkMode,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingListItem(
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    val localSetting by localSettingManager.localSettingState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    ListItem(
        modifier = Modifier.clickable {
            expanded = true
        },
        headlineContent = {
            Text("主题")
        },
        supportingContent = {
            Text(themeTextMap[localSetting.theme]!!)
        },
        trailingContent = {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                }
            ) {
                Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = "展开设置")
                ExposedDropdownMenu(
                    modifier = Modifier.width(200.dp),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    themeTextMap.forEach { (theme, label) ->
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = themeIconMap[theme]!!,
                                    contentDescription = label
                                )
                            },
                            trailingIcon = {
                                if (localSetting.theme == theme) {
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
                                localSettingManager.updateTheme(theme)
                            }
                        )
                    }
                }
            }
        }
    )

}