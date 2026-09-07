package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
        LazyColumn(
            modifier = Modifier.padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SettingGroup("基础") {
                    ThemeSettingListItem()
                    HorizontalDivider(color = MaterialTheme.colorScheme.background)
                    ApiSettingListItem()
                }
            }
            item {
                ComicCoverCacheListItem()
            }
            item {
                ComicOriginalPicCacheListItem()
            }
            item {
                ComicDecodePicCacheListItem()
            }
        }
    }
}