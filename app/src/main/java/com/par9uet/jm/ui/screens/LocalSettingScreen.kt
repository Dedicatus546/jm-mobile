package com.par9uet.jm.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.components.SelectDialog
import com.par9uet.jm.ui.components.SelectOption
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSettingScreen(
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    val localSetting by localSettingManager.localSetting.collectAsState()
    var isOpenSettingSelectDialog by remember { mutableStateOf(false) }
    val apiSelectOptionList = remember(localSetting.apiList) {
        derivedStateOf {
            localSetting.apiList.map {
                // label 去除 https://
                SelectOption(it.substring(8), it)
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val scrollBehavior =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "设置",
                        color = MaterialTheme.colorScheme.surface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            ListItem(
                modifier = Modifier.clickable(onClick = {
                    isOpenSettingSelectDialog = true
                }),
                leadingContent = {
                    Icon(Icons.Default.Api, "api")
                },
                headlineContent = {
                    Text("API 接口")
                },
                supportingContent = {
                    Text(localSetting.api)
                }
            )
        }
        if (isOpenSettingSelectDialog) {
            SelectDialog(
                title = "API 接口",
                value = localSetting.api,
                selectOptionList = apiSelectOptionList.value,
                onSelect = {

                },
                onDismissRequest = {

                }
            )
        }
    }
}