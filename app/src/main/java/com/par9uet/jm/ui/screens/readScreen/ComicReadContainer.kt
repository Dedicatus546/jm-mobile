package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.ReadMode
import com.par9uet.jm.ui.components.ErrorTips
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.ui.viewModel.BaseComicReadViewModel

@Composable
fun ComicReadContainer(
    comicId: Int,
    comicReadViewModel: BaseComicReadViewModel,
) {
    val localSettingManager = LocalLocalSettingManager.current
    val isShowToolbar by comicReadViewModel.isShowToolBar.collectAsState()
    val size by comicReadViewModel.sizeState.collectAsState()
    val currentIndex by comicReadViewModel.currentIndex.collectAsState()
    val localSetting by localSettingManager.localSettingState.collectAsState()
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()

    LaunchedEffect(Unit) {
        comicReadViewModel.load(comicId)
    }

    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .fillMaxSize()
    ) {
        if (comicPicState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (comicPicState.isError) {
            ErrorTips(
                errorMsg = comicPicState.errorMsg
            ) {
                comicReadViewModel.retry(comicId)
            }
        } else {
            if (localSetting.readMode == ReadMode.SCROLL) {
                ComicScrollRead(
                    comicReadViewModel = comicReadViewModel
                )
            } else {
                ComicPageRead(
                    comicReadViewModel = comicReadViewModel
                )
            }
        }
        // 页码显示
        if (localSetting.showPageNumber && comicPicState.isOk) {
            SuggestionChip(
                border = null,
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
                    .statusBarsPadding()
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp),
                onClick = {},
                label = {
                    Text("${currentIndex + 1} / $size")
                }
            )
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = isShowToolbar,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut()
        ) {
            ToolsBar(
                comicReadViewModel = comicReadViewModel
            )
        }
        if (localSetting.showComicPageReadTip
            && (localSetting.readMode == ReadMode.PAGE_RIGHT || localSetting.readMode == ReadMode.PAGE_LEFT)
            || localSetting.showComicScrollReadTip && localSetting.readMode == ReadMode.SCROLL
        ) {
            Tip(
                readMode = localSetting.readMode,
            )
            TipCloseButton(
                modifier = Modifier.align(
                    if (localSetting.readMode == ReadMode.SCROLL) Alignment.CenterEnd else Alignment.BottomCenter
                ).let {
                    if (localSetting.readMode == ReadMode.SCROLL) {
                        it.padding(end = 40.dp)
                    } else {
                        it.padding(bottom = 40.dp)
                    }
                },
                onClick = {
                    if (localSetting.readMode == ReadMode.SCROLL) {
                        localSettingManager.closeShowComicScrollReadTip()
                    } else {
                        localSettingManager.closeShowComicPageReadTip()
                    }
                }
            )
        }
    }
}