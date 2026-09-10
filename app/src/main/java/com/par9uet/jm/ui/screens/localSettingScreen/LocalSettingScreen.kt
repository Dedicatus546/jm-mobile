package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.components.CommonScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSettingScreen() {
    CommonScaffold(
        title = "设置"
    ) {
        LazyColumn(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                BaseSettingGroup()
            }
            item {
                ImageCacheSettingGroup()
            }
            // item {
            //     LogSettingListItem()
            // }
            item {
                OtherSettingGroup()
            }
        }
    }
}