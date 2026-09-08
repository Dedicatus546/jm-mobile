package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.components.SettingGroup
import com.par9uet.jm.ui.components.SettingListItem
import com.par9uet.jm.ui.provider.LocalLocalSettingManager

private val logLevelTextMap = mapOf(
    "info" to "info",
    "debug" to "debug"
)

@Composable
fun LogSettingListItem() {
    val localSettingManager = LocalLocalSettingManager.current
    val localSetting by localSettingManager.localSettingState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    SettingGroup(
        title = "日志"
    ) {
        SettingListItem(
            icon = Icons.Default.BugReport,
            iconContentDescription = "",
            title = "日志等级",
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
                Text(localSetting.logLevel)
                ExposedDropdownMenu(
                    modifier = Modifier.width(200.dp),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    logLevelTextMap.forEach { (theme, label) ->
                        DropdownMenuItem(
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

                            }
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.background)
        SettingListItem(
            icon = Icons.Default.Autorenew,
            iconContentDescription = "导出日志文件",
            title = "导出日志文件",
            onClick = {
                // TODO
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "点击进入",
            )
        }
    }
}