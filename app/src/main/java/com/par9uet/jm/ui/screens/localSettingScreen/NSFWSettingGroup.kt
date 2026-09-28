package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.par9uet.jm.ui.components.SettingGroup
import com.par9uet.jm.ui.components.SettingListItem
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.utils.formatFileSize

@Composable
fun NSFWSettingGroup() {
    val localSettingManager = LocalLocalSettingManager.current
    val localSetting by localSettingManager.localSettingState.collectAsState()

    SettingGroup(title = "NSFW") {
        SettingListItem(
            icon = Icons.Default.ColorLens,
            iconContentDescription = "",
            title = "启用计算器应用图标",
            description = "启用/关闭后将会立即退出应用"
        ) {
            Switch(
                checked = localSetting.enableFakeAppIcon,
                onCheckedChange = {
                    localSettingManager.updateEnableFakeAppIcon(it)
                }
            )
        }
    }
}