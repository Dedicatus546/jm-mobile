package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.par9uet.jm.ui.components.SettingGroup
import com.par9uet.jm.ui.components.SettingListItem
import com.par9uet.jm.ui.provider.LocalLocalSettingManager

@Composable
fun NSFWSettingGroup() {
    val localSettingManager = LocalLocalSettingManager.current
    val localSetting by localSettingManager.localSettingState.collectAsState()

    SettingGroup(title = "NSFW") {
        SettingListItem(
            icon = Icons.Default.ColorLens,
            iconContentDescription = "",
            title = "伪装为计算器应用",
            description = "启用/关闭后将会立即退出应用"
        ) {
            Switch(
                checked = localSetting.enableFakeApp,
                onCheckedChange = {
                    localSettingManager.updateEnableFakeApp(it)
                }
            )
        }
    }
}