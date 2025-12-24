package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.viewModel.ComicDetailViewModel
import com.par9uet.jm.utils.log
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun ComicChapterScreen(
    comicId: Int,
    comicDetailViewModel: ComicDetailViewModel = koinActivityViewModel(),
) {
    val comicDetailState by comicDetailViewModel.comicDetailState.collectAsState()
    val comicChapterList = comicDetailState.data?.comicChapterList ?: listOf()
    val mainNavController = LocalMainNavController.current

    LaunchedEffect(Unit) {
        log("comicChapterList $comicChapterList")
    }

    CommonScaffold(title = "选择章节") {
        LazyVerticalGrid(
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            columns = GridCells.Fixed(2)
        ) {
            itemsIndexed(comicChapterList, key = { _, item -> item.id }) { index, item ->
                AssistChip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        mainNavController.navigate("comicRead/${item.id}")
                    },
                    label = {
                        Text(
                            text = item.name.ifBlank { "第${index}话" },
                        )
                    })
            }
        }
    }
}
