package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.SettingGroup
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.ui.viewModel.ApiSelectViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiSelectScreen() {
    val localSettingManager = LocalLocalSettingManager.current
    val apiSelectViewModel: ApiSelectViewModel = hiltViewModel()
    val localSetting by localSettingManager.localSettingState.collectAsState()
    val pullApiListState by apiSelectViewModel.pullApiListState.collectAsState()

    CommonScaffold(
        title = "API 接口"
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SettingGroup {
                    localSetting.apiList.forEach {
                        ListItem(
                            modifier = Modifier.selectable(
                                selected = (it == localSetting.api),
                                onClick = {
                                    localSettingManager.updateApi(it)
                                },
                                role = Role.RadioButton
                            ),
                            leadingContent = {
                                RadioButton(
                                    selected = (it == localSetting.api),
                                    onClick = null // null recommended for accessibility with screen readers
                                )
                            },
                            trailingContent = {
                                IconButton(onClick = {
                                    localSettingManager.removeApi(it)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "删除"
                                    )
                                }
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(it)
                        }
                    }
                }
            }
            item {
                val coroutineScope = rememberCoroutineScope()
                val sheetState = rememberBottomSheetState(
                    initialValue = SheetValue.Hidden,
                )
                var showBottomSheet by remember { mutableStateOf(false) }
                val focusRequester = remember { FocusRequester() }
                val focusManager = LocalFocusManager.current
                val textFieldState = rememberTextFieldState()
                fun addApi() {
                    val api = textFieldState.text.toString()
                    if (api.isBlank()) {
                        return
                    }
                    // TODO 这里要加个格式校验
                    localSettingManager.addApi(api)
                    focusManager.clearFocus()
                    textFieldState.clearText()
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                }
                LaunchedEffect(showBottomSheet) {
                    // TODO fix 这里似乎 UI 会有错误，等测试下 release 下有没有问题
                    if (showBottomSheet) {
                        focusRequester.requestFocus()
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showBottomSheet = true
                    }
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加"
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("添加一个 API 接口")
                }
                if (showBottomSheet) {
                    ModalBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = {
                            showBottomSheet = false
                        }
                    ) {
                        TextField(
                            placeholder = {
                                Text("以 http:// 或 https:// 开头")
                            },
                            lineLimits = TextFieldLineLimits.SingleLine,
                            state = textFieldState,
                            label = {
                                Text("API 接口")
                            },
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                                .focusRequester(focusRequester)
                                .fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            onKeyboardAction = {
                                addApi()
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            modifier = Modifier
                                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                                .align(Alignment.End),
                            onClick = {
                                addApi()
                            }
                        ) {
                            Icon(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                imageVector = Icons.Default.Done,
                                contentDescription = "确认"
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("确认")
                        }
                    }
                }
            }
            item {
                Button(
                    enabled = !pullApiListState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        apiSelectViewModel.pullApiList()
                    }
                ) {
                    if (pullApiListState.isLoading) {
                        CircularProgressIndicator(
                            color = ButtonDefaults.buttonColors().disabledContainerColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("拉取中...")
                    } else {
                        Icon(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            imageVector = Icons.Default.Download,
                            contentDescription = "下载"
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("从远程拉取最新 API 列表")
                    }
                }
            }
        }
    }
}