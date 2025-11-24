package com.par9uet.jm.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.viewModel.GlobalViewModel
import com.par9uet.jm.viewModel.SettingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ComicImage(
    comic: Comic,
    showIdChip: Boolean = false,
    showReadCountChip: Boolean = false,
    showLikeCountChip: Boolean = false,
    globalViewModel: GlobalViewModel= koinViewModel()
) {
    val settingState = globalViewModel.settingState
    val model = "${settingState.setting.imgHost}/media/albums/1230228_3x4.jpg"
//    val model = "${settingState.setting.imgHost}/media/albums/${comic.id}_3x4.jpg"
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = model,
            contentDescription = "${comic.name}的封面",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .aspectRatio(3f / 4f)
                .fillMaxWidth()
        )
        if (showIdChip) {
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                CompositionLocalProvider(
                    // 去除 m3 默认的最小高度
                    LocalMinimumInteractiveComponentSize provides Dp.Unspecified
                ) {
                    AssistChip(
                        border = null,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                        modifier = Modifier.padding(end = 10.dp, bottom = 10.dp),
                        onClick = {

                        },
                        label = {
                            Text("JM${comic.id}")
                        }
                    )
                }
            }
        }
        if (showLikeCountChip || showReadCountChip) {
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                CompositionLocalProvider(
                    // 去除 m3 默认的最小高度
                    LocalMinimumInteractiveComponentSize provides Dp.Unspecified
                ) {
                    Column(
                        modifier = Modifier.padding(end = 10.dp, top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        if (showLikeCountChip) {
                            AssistChip(
                                border = null,
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                ),
                                onClick = {
                                    // TODO
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Favorite,
                                        contentDescription = "喜爱人数",
                                        tint = Color.Red
                                    )
                                },
                                label = {
                                    Text(comic.likeCount.toString())
                                }
                            )
                        }
                        if (showReadCountChip) {
                            AssistChip(
                                border = null,
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                ),
                                onClick = {
                                    // TODO
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Visibility, contentDescription = "阅读人数")
                                },
                                label = {
                                    Text(comic.readCount.toString())
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}