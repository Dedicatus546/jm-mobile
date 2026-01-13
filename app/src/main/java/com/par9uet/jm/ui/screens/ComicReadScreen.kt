package com.par9uet.jm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.par9uet.jm.data.models.ImageResultState
import com.par9uet.jm.store.LocalSettingManager
import com.par9uet.jm.ui.components.ComicPicImage
import com.par9uet.jm.ui.components.DashedHorizontalDivider
import com.par9uet.jm.ui.components.DashedVerticalDivider
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import com.par9uet.jm.utils.log
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@OptIn(FlowPreview::class)
@Composable
private fun ComicScrollRead(
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
) {
    var showTip by remember { mutableStateOf(true) }
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val currentIndexState by comicReadViewModel.currentIndexState.collectAsState()
    val list = comicPicState.data ?: listOf()
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    LaunchedEffect(currentIndexState, lazyListState) {
        if (currentIndexState == lazyListState.firstVisibleItemIndex) {
            return@LaunchedEffect
        }
        lazyListState.animateScrollToItem(currentIndexState)
    }
    LaunchedEffect(lazyListState, list) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .debounce(200L)
            .collect { index ->
                comicReadViewModel.changeIndex(index, context)
            }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        // 1. 在 Initial 阶段观察按下，不消耗事件，确保 Pager 能收到
                        val down =
                            awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial
                            )
                        // 2. 等待抬起
                        val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        // 3. 判定逻辑：只有在没被消费（说明不是滑动）且距离很短时触发
                        if (up != null && !up.isConsumed) {
                            val distance = (up.position - down.position).getDistance()
                            if (distance < 10.dp.toPx()) {
                                // --- 获取点击位置 ---
                                val screenHeight = size.height
                                val clickY = up.position.y

                                when {
                                    clickY < screenHeight / 3 -> {
                                        comicReadViewModel.prevIndex(context)
                                    }

                                    clickY > screenHeight * 2 / 3 -> {
                                        comicReadViewModel.nextIndex(context)
                                    }

                                    else -> {
                                        log("点击中间：菜单")
                                    }
                                }
                            }
                        }
                    }
                }
            }
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(list, key = {
                "${it.comicId}_${it.originSrc}"
            }) {
                ComicPicImage(
                    comicPicImageState = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(
                            when (val state = it.imageResultState) {
                                is ImageResultState.Success -> {
                                    state.decodeImageAspectRatio
                                }

                                else -> {
                                    9f / 16
                                }
                            }
                        )
                )
            }
        }
        if (showTip) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = .7f)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "上一页",
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
                DashedHorizontalDivider(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(.8f)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "", tint = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "工具栏",
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 40.dp),
                        onClick = {
                            showTip = false
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Check, contentDescription = "", tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "知道了", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
                DashedHorizontalDivider(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(.8f)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "", tint = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "下一页",
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ComicPageRead(
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
) {
    var showTip by remember { mutableStateOf(true) }
    val currentIndexState by comicReadViewModel.currentIndexState.collectAsState()
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val list = comicPicState.data ?: listOf()
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = 0) {
        list.size
    }
    LaunchedEffect(currentIndexState) {
        if (currentIndexState == pagerState.currentPage) {
            return@LaunchedEffect
        }
        pagerState.animateScrollToPage(currentIndexState)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { index ->
                comicReadViewModel.changeIndex(index, context)
            }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        // 1. 在 Initial 阶段观察按下，不消耗事件，确保 Pager 能收到
                        val down =
                            awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial
                            )
                        // 2. 等待抬起
                        val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        // 3. 判定逻辑：只有在没被消费（说明不是滑动）且距离很短时触发
                        if (up != null && !up.isConsumed) {
                            val distance = (up.position - down.position).getDistance()
                            if (distance < 10.dp.toPx()) {
                                // --- 获取点击位置 ---
                                val screenWidth = size.width
                                val clickX = up.position.x

                                when {
                                    clickX < screenWidth / 3 -> {
                                        comicReadViewModel.prevIndex(context)
                                    }

                                    clickX > screenWidth * 2 / 3 -> {
                                        comicReadViewModel.nextIndex(context)
                                    }

                                    else -> {
                                        log("点击中间：菜单")
                                    }
                                }
                            }
                        }
                    }
                }
            }
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState
        ) { page ->
            val item = list[page]
            ComicPicImage(
                comicPicImageState = item,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        if (showTip) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = .7f)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "上一页",
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
                DashedVerticalDivider(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxHeight(.8f)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "", tint = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "工具栏",
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 40.dp),
                        onClick = {
                            showTip = false
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Check, contentDescription = "", tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "知道了", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
                DashedVerticalDivider(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxHeight(.8f)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "", tint = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "下一页",
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun ComicReadScreen(
    comicId: Int,
    comicReadViewModel: ComicReadViewModel = koinViewModel(),
    localSettingManager: LocalSettingManager = getKoin().get()
) {
    val comicPicState by comicReadViewModel.comicPicState.collectAsState()
    val loading = comicPicState.isLoading
    val localSetting by localSettingManager.localSettingState.collectAsState()

    LaunchedEffect(Unit) {
        comicReadViewModel.getComicPicList(
            comicId,
            localSettingManager.localSettingState.value.shunt
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            if (localSetting.readMode == "scroll") {
                ComicScrollRead()
            } else {
                ComicPageRead()
            }
        }
    }
}