package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.par9uet.jm.ui.components.SettingListItem
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.ui.provider.LocalMainNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiSettingListItem() {
    val localSettingManager = LocalLocalSettingManager.current
    val mainNavController = LocalMainNavController.current
    val localSetting by localSettingManager.localSettingState.collectAsState()
    SettingListItem(
        icon = Icons.Default.Api,
        iconContentDescription = "API",
        title = "API 接口",
        onClick = {
            mainNavController.navigate("apiSelect")
        }
    ) {
        Text(localSetting.api)
    }
}