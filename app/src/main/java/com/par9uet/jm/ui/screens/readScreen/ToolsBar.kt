package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsBar(
    modifier: Modifier = Modifier,
    sliderState: SliderState,
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
) {
    val currentIndexState by comicReadViewModel.currentIndexState
    val size = comicReadViewModel.size
    val context = LocalContext.current
    Card(
        modifier = modifier
            .padding(
                start = 20.dp,
                bottom = 20.dp,
                end = 20.dp
            )
//            .clickable(
//                onClick = {}, // 空实现，仅用于消费和拦截手势
//                indication = null, // 去掉点击水波纹
//                interactionSource = remember { MutableInteractionSource() }
//            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    enabled = currentIndexState > 0,
                    onClick = {
                        comicReadViewModel.prev(context, false)
                    }
                ) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "上一张")
                }
                Slider(
                    modifier = Modifier
                        .weight(1f),
                    state = sliderState,
                    track = { sliderState ->
                        SliderDefaults.Track(
                            modifier = Modifier.height(10.dp),
                            sliderState = sliderState,
                            colors = SliderDefaults.colors(
                                activeTickColor = Color.Transparent,
                                inactiveTickColor = Color.Transparent
                            ),
                            thumbTrackGapSize = 0.dp,
                            drawStopIndicator = null
                        )
                    },
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(20.dp) // 设置圆点大小
                                .border(
                                    width = 4.dp,
                                    color = SliderDefaults.colors().thumbColor,
                                    CircleShape
                                )
                                .background(
                                    SliderDefaults.colors().inactiveTrackColor,
                                    CircleShape
                                ) // 设置颜色和圆形形状
                        )
                    }
                )
                IconButton(
                    enabled = currentIndexState < size,
                    onClick = {
                        comicReadViewModel.next(context, false)
                    }
                ) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "下一张")
                }
            }
            // TODO 切换模式
            // TODO 切换预载数量
//            Row() {
//                IconButton(
//                    onClick = {
//                    }
//                ) {
//                    Icon(imageVector = Icons.Default.Settings, contentDescription = "设置")
//                }
//            }
        }
    }
}