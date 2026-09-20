package com.par9uet.jm.ui.screens.comicChapterScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.ComicChapter
import com.par9uet.jm.router.ComicReadRoute
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.provider.LocalMainNavController
import com.par9uet.jm.ui.state.rememberTabIndexState
import com.par9uet.jm.ui.viewModel.ComicChapterViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicChapterScreen(
    comic: Comic,
    comicChapterList: List<ComicChapter>,
) {
    val comicChapterViewModel: ComicChapterViewModel = hiltViewModel()

    val mainNavController = LocalMainNavController.current

    val coroutineScope = rememberCoroutineScope()
    val groupSize = 30
    val chapterGroup by remember(comicChapterList) {
        derivedStateOf {
            comicChapterList.withIndex().groupBy {
                val start = it.index / groupSize * groupSize + 1
                val end = (start + groupSize - 1).coerceAtMost(comicChapterList.size)
                "$start-$end"
            }.mapValues { it.value.map { item -> item.value } }.toList().reversed()
        }
    }
    val pagerState = rememberPagerState(initialPage = 0) {
        chapterGroup.size
    }
    val selectedTabIndexState = rememberTabIndexState()

    var currentSelectComicChapter by remember { mutableStateOf<ComicChapter?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(
            SheetValue.Hidden,
            SheetValue.Expanded
        )
    )

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndexState.value = pagerState.currentPage
    }

    CommonScaffold(title = "选择章节") {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PrimaryScrollableTabRow(
                containerColor = Color.Transparent,
                selectedTabIndex = selectedTabIndexState.value,
                edgePadding = 0.dp,
                scrollState = rememberScrollState()
            ) {
                chapterGroup.forEachIndexed { index, item ->
                    key(item.first) {
                        Tab(
                            selected = selectedTabIndexState.value == index,
                            onClick = {
                                selectedTabIndexState.value = index
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(selectedTabIndexState.value)
                                }
                            },
                            text = {
                                Text(
                                    text = item.first,
                                    maxLines = 1,
                                )
                            }
                        )
                    }
                }
            }
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = pagerState
            ) { page ->
                val list = chapterGroup[page].second
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        contentPadding = PaddingValues(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        columns = GridCells.Fixed(1),
                    ) {
                        itemsIndexed(list, key = { _, item -> item.id }) { index, item ->
                            ListItem(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        mainNavController.navigate(
                                            ComicReadRoute(
                                                comicId = item.id
                                            )
                                        )
                                    }
                                ),
                                trailingContent = {
                                    IconButton(onClick = {
                                        currentSelectComicChapter = item.copy()
                                        showBottomSheet = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "显示操作"
                                        )
                                    }
                                }
                            ) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "${index + (chapterGroup.size - page - 1) * groupSize + 1}. ${item.name}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (showBottomSheet && currentSelectComicChapter != null) {
        ComicChapterBottomActionSheet(
            comic = comic,
            comicChapter = currentSelectComicChapter!!,
            sheetState = sheetState,
            onDismissRequest = {
                showBottomSheet = false
            },
            comicChapterViewModel = comicChapterViewModel
        )
    }
}