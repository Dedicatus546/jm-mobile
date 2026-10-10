package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.par9uet.jm.ui.components.SettingGroup
import com.par9uet.jm.ui.components.SettingListItem
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NSFWSettingGroup() {
    val localSettingManager = LocalLocalSettingManager.current
    val localSetting by localSettingManager.localSettingState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val textFieldState = rememberTextFieldState()
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
    )
    val isPasswordError by remember {
        derivedStateOf {
            val text = textFieldState.text
            text.isNotEmpty() && (text.length !in 4..9)
        }
    }

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.isAnimationRunning }
            .collect { isRunning ->
                if (!isRunning && sheetState.isVisible) {
                    focusRequester.requestFocus()
                }
            }
    }

    SettingGroup(title = "NSFW") {
        SettingListItem(
            icon = Icons.Default.ColorLens,
            iconContentDescription = "",
            title = "伪装为计算器应用",
            description = "启用/关闭后将会立即退出应用"
        ) {
            Switch(
                checked = localSetting.enableFakeApp,
                onCheckedChange = {
                    localSettingManager.updateEnableFakeApp(it)
                }
            )
        }
        SettingListItem(
            icon = Icons.Default.Password,
            iconContentDescription = "",
            title = "解锁密码",
            description = "输入该数字后按 C(clear) 解锁进入主界面",
            onClick = {
                showBottomSheet = true
            }
        ) {
            Text(
                text = localSetting.unLockFakeAppPassword
            )
            if (showBottomSheet) {
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = {
                        showBottomSheet = false
                    }
                ) {
                    // 这里 keyboardController 必须在 ModalBottomSheet 内获取
                    // 不然得到的是 Activity 级别的 controller，它管不到弹层里的键盘
                    val keyboardController = LocalSoftwareKeyboardController.current
                    val applyPassword: () -> Unit = {
                        val password = textFieldState.text.toString()
                        if (!isPasswordError) {
                            localSettingManager.updateUnLockFakeAppPassword(password)
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            textFieldState.clearText()
                            coroutineScope.launch {
                                sheetState.hide()
                            }
                        }
                    }
                    TextField(
                        placeholder = {
                            Text("必须为纯数字，最短 4 位，最长 9 位")
                        },
                        lineLimits = TextFieldLineLimits.SingleLine,
                        state = textFieldState,
                        inputTransformation = InputTransformation
                            .maxLength(9)
                            .then {
                                if (!asCharSequence().isDigitsOnly()) {
                                    revertAllChanges() // 非数字则回退
                                }
                            },
                        supportingText = {
                            if (isPasswordError) {
                                Text("请输入符合要求的密码")
                            }
                        },
                        label = {
                            Text("解锁密码")
                        },
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                            .focusRequester(focusRequester)
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        onKeyboardAction = {
                            applyPassword()
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        modifier = Modifier
                            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                            .align(Alignment.End),
                        onClick = {
                            applyPassword()
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
    }
}