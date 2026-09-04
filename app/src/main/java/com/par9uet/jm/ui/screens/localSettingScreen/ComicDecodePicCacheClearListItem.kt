package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ComicDecodePicCacheClearListItem() {
    ListItem(
        headlineContent = {
            Text("解密图片缓存")
        },
        supportingContent = {
            val data = 200
            Text("当前已使用 $data M")
        },
        trailingContent = {
            TextButton(onClick = {

            }) {
                Text("清除")
            }
        }
    )
}