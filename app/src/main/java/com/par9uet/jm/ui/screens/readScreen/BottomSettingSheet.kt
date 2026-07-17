package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material.icons.filled.SwipeRight
import androidx.compose.material.icons.filled.SwipeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.utils.log
import kotlinx.coroutines.launch
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSettingSheet(
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit,
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    // 在此处我们只做值的变化，对应的系统动作得放到 Read 页面执行
    val coroutineScope = rememberCoroutineScope()
    val localSetting by localSettingManager.localSettingState.collectAsState()
    val sliderState = rememberSliderState(
        value = localSetting.brightness,
        valueRange = 0f..1f
    )
    sliderState.onValueChangeFinished = {
        coroutineScope.launch {
            val sliderValue = sliderState.value
            localSettingManager.updateBrightness(sliderValue)
        }
    }

    LaunchedEffect(localSetting.brightnessFollowSystem, localSetting.brightness) {
        if (localSetting.brightnessFollowSystem) {
            log("BottomSettingSheet", "update sliderState value ${localSetting.brightness}")
            sliderState.value = localSetting.brightness
        }
    }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column {
            ListItem(
                colors = ListItemDefaults.colors().copy(
                    containerColor = Color.Transparent
                ),
                headlineContent = {
                    Text("亮度跟随系统")
                },
                trailingContent = {
                    Switch(checked = localSetting.brightnessFollowSystem, onCheckedChange = {
                        localSettingManager.updateBrightnessFollowSystem(it)
                    })
                }
            )
            ListItem(
                colors = ListItemDefaults.colors().copy(
                    containerColor = Color.Transparent
                ),
                headlineContent = {
                    Text("亮度调节")
                },
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = "暗"
                        )
                        Slider(
                            modifier = Modifier
                                .width(150.dp),
                            state = sliderState,
                            enabled = !localSetting.brightnessFollowSystem
                        )
                        Icon(
                            imageVector = Icons.Default.LightMode,
                            contentDescription = "亮"
                        )
                    }
                }
            )
            ListItem(
                colors = ListItemDefaults.colors().copy(
                    containerColor = Color.Transparent
                ),
                headlineContent = {
                    Text("阅读模式")
                },
                trailingContent = {
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 0,
                                count = 3
                            ),
                            onClick = {
                                localSettingManager.updateReadMode("page")
                            },
                            selected = localSetting.readMode == "page",
                            label = {
                                Column() {
                                    Icon(
                                        imageVector = Icons.Default.SwipeLeft,
                                        contentDescription = "翻页模式"
                                    )
                                }
                            }
                        )
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 1,
                                count = 3
                            ),
                            onClick = {
                                localSettingManager.updateReadMode("pageReverse")
                            },
                            selected = localSetting.readMode == "pageReverse",
                            label = {
                                Column {
                                    Icon(
                                        imageVector = Icons.Default.SwipeRight,
                                        contentDescription = "反转翻页模式"
                                    )
                                }
                            }
                        )
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 2,
                                count = 3
                            ),
                            onClick = {
                                localSettingManager.updateReadMode("scroll")
                            },
                            selected = localSetting.readMode == "scroll",
                            label = {
                                Column {
                                    Icon(
                                        imageVector = Icons.Default.SwipeUp,
                                        contentDescription = "滚动模式"
                                    )
                                }
                            }
                        )
                    }
                }
            )
            ListItem(
                colors = ListItemDefaults.colors().copy(
                    containerColor = Color.Transparent
                ),
                headlineContent = {
                    Text("分流")
                },
                trailingContent = {
                    SingleChoiceSegmentedButtonRow {
                        for (i in 0..<4) {
                            val shunt = (i + 1).toString()
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = i,
                                    count = 4
                                ),
                                onClick = {
                                    localSettingManager.updateShunt(shunt)
                                },
                                selected = localSetting.shunt == shunt,
                                label = {
                                    Text(text = shunt)
                                }
                            )
                        }

                    }
                }
            )
            ListItem(
                colors = ListItemDefaults.colors().copy(
                    containerColor = Color.Transparent
                ),
                headlineContent = {
                    Text("显示页码")
                },
                trailingContent = {
                    Switch(checked = localSetting.showPageNumber, onCheckedChange = {
                        localSettingManager.updateShowPageNumber(it)
                    })
                }
            )
            ListItem(
                colors = ListItemDefaults.colors().copy(
                    containerColor = Color.Transparent
                ),
                headlineContent = {
                    Text("屏幕常亮")
                },
                trailingContent = {
                    Switch(checked = localSetting.noLockScreen, onCheckedChange = {
                        localSettingManager.updateNoLockScreen(it)
                    })
                }
            )
            ListItem(
                colors = ListItemDefaults.colors().copy(
                    containerColor = Color.Transparent
                ),
                headlineContent = {
                    Text("图片缩放")
                },
                trailingContent = {
                    Switch(checked = localSetting.supportZoom, onCheckedChange = {
                        localSettingManager.updateSupportZoom(it)
                    })
                }
            )
        }
    }
}