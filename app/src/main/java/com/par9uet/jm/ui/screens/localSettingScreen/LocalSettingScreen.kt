package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.par9uet.jm.ui.components.CommonScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSettingScreen() {
    CommonScaffold(
        title = "设置"
    ) {
        Column {
            ThemeSettingListItem()
            ApiSettingListItem()
        }
    }
}