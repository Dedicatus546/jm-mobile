package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.par9uet.jm.ui.components.SettingGroup
import com.par9uet.jm.ui.components.SettingListItem
import com.par9uet.jm.ui.provider.LocalMainNavController

@Composable
fun OtherSettingListItem() {
    val mainNavController = LocalMainNavController.current
    SettingGroup(
        title = "其他"
    ) {
        SettingListItem(
            icon = Icons.Default.Info,
            iconContentDescription = "关于",
            title = "关于",
            onClick = {
                mainNavController.navigate("about")
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "点击进入",
            )
        }
    }
}