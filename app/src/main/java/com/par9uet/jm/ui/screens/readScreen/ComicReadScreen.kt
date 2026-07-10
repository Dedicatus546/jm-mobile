package com.par9uet.jm.ui.screens.readScreen

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin
import kotlin.math.max

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
fun ComicReadScreen(
    comicId: Int,
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isShowToolbar by comicReadViewModel.isShowToolBar
    val size = comicReadViewModel.size
    var currentIndexState by comicReadViewModel.currentIndexState
    val localSetting by localSettingManager.localSettingState.collectAsState()
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()

    // 获取图片列表并且解码第一张图片
    LaunchedEffect(Unit) {
        comicReadViewModel.getComicPicList(
            comicId,
            localSettingManager.localSettingState.value.shunt
        ) {
            comicReadViewModel.decodeIndex(0, context)
        }
    }

    val view = LocalView.current
    val controller = remember(view) {
        val window = (context as? Activity)?.window
        WindowInsetsControllerCompat(window!!, view).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    LaunchedEffect(isShowToolbar) {
        if (isShowToolbar) {
            controller.show(WindowInsetsCompat.Type.statusBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.statusBars())
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            controller.show(WindowInsetsCompat.Type.statusBars())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (comicPicState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (comicPicState.isError) {
            // TODO 错误情况
        } else {
            // 放 else 内初始化，不然 steps valueRange 会取到 0 导致 UI 错误
            // 这里遵循谁变化，谁调用 decodeIndex
            val sliderState = rememberSliderState(
                value = currentIndexState.toFloat(),
                steps = max(0, size - 2),
                valueRange = 0f..max(0, size - 1).toFloat(),
            )
            sliderState.onValueChangeFinished = {
                coroutineScope.launch {
                    val sliderValue = sliderState.value.toInt()
                    if (currentIndexState != sliderValue) {
                        currentIndexState = sliderValue
                        comicReadViewModel.decodeIndex(currentIndexState, context)
                    }
                }
            }
            // pager 或者 scroll 变更
            LaunchedEffect(currentIndexState) {
                val sliderValue = sliderState.value.toInt()
                if (currentIndexState != sliderValue) {
                    sliderState.value = currentIndexState.toFloat()
                }
            }
            if (localSetting.readMode == "scroll") {
                ComicScrollRead()
            } else {
                ComicPageRead()
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
                ToolsBar(sliderState = sliderState)
            }
            if (localSetting.showComicPageReadTip && localSetting.readMode == "page" || localSetting.showComicScrollReadTip && localSetting.readMode == "scroll") {
                Tip(
                    readMode = localSetting.readMode,
                )
                TipCloseButton(
                    modifier = Modifier.align(
                        if (localSetting.readMode == "scroll") Alignment.CenterEnd else Alignment.BottomCenter
                    ).let {
                        if (localSetting.readMode == "scroll") {
                            it.padding(end = 40.dp)
                        } else {
                            it.padding(bottom = 40.dp)
                        }
                    },
                    onClick = {
                        if (localSetting.readMode == "scroll") {
                            localSettingManager.closeShowComicScrollReadTip()
                        } else {
                            localSettingManager.closeShowComicPageReadTip()
                        }
                    }
                )
            }
        }
    }
}