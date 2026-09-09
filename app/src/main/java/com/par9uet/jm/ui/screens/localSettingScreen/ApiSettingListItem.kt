package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.par9uet.jm.ui.components.SettingListItem
import com.par9uet.jm.ui.provider.LocalLocalSettingManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiSettingListItem() {
    val localSettingManager = LocalLocalSettingManager.current
    val localSetting by localSettingManager.localSettingState.collectAsState()
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
    )
    var showBottomSheet by remember { mutableStateOf(false) }
    SettingListItem(
        icon = Icons.Default.Api,
        iconContentDescription = "API",
        title = "API 接口",
        onClick = {
            showBottomSheet = true
        }
    ) {
        Text(localSetting.api)
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                showBottomSheet = false
            }
        ) {
            localSetting.apiList.forEach {
                ListItem(
                    modifier = Modifier
                        .clickable(onClick = {
                            localSettingManager.updateApi(it)
                        }),
                    trailingContent = {
                        if (localSetting.api == it) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "选中$it")
                        }
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(it)
                }
            }
        }
    }
}