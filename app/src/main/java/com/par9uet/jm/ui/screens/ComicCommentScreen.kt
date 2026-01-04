package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.ui.components.Comment
import com.par9uet.jm.ui.components.CommentSkeleton
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.components.PullRefreshAndLoadMoreGrid
import com.par9uet.jm.ui.viewModel.ComicDetailViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
private fun CommentListSkeleton() {
    FlowRow(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (i in 0 until 10) {
            key(i) {
                CommentSkeleton()
            }
        }
    }
}

@Composable
fun ComicCommentScreen(
    comicId: Int,
    comicDetailViewModel: ComicDetailViewModel = koinActivityViewModel(),
) {
    val commentLazyPagingItems = comicDetailViewModel.commentPager.collectAsLazyPagingItems()
    LaunchedEffect(Unit) {
        comicDetailViewModel.changeCommentComicId(comicId)
    }
    CommonScaffold(
        title = "评论"
    ) {
        if (commentLazyPagingItems.loadState.refresh is LoadState.Loading && commentLazyPagingItems.itemCount == 0) {
            CommentListSkeleton()
            return@CommonScaffold
        }
        PullRefreshAndLoadMoreGrid(
            lazyPagingItems = commentLazyPagingItems,
            key = { it.id },
            columns = GridCells.Fixed(1)
        ) {
            Comment(it)
        }
    }
}