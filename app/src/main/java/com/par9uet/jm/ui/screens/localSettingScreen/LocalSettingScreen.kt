package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.par9uet.jm.ui.components.CommonScaffold

@Composable
private fun SettingGroupTitle(title: String) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSettingScreen() {
    CommonScaffold(
        title = "设置"
    ) {
        Column {
            SettingGroupTitle("基础")
            ThemeSettingListItem()
            ApiSettingListItem()
            HorizontalDivider()
            SettingGroupTitle("存储")
            ComicCoverCacheClearListItem()
            ComicOriginalPicCacheClearListItem()
            ComicDecodePicCacheClearListItem()
        }
    }
}