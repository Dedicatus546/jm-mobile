package com.par9uet.jm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.par9uet.jm.data.models.Comment
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
private fun ReplayComment(comment: Comment) {
    val annotatedString = buildAnnotatedString {
        // 设置用户名的样式
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append(comment.username)
        }
        append(": ")
        append(
            AnnotatedString.fromHtml(
                htmlString = comment.content,
            ).trim()
        )
    }
    Text(
        text = annotatedString,
        softWrap = true,
        fontSize = 12.sp
    )
}

@Composable
private fun CommentWithAction(comment: Comment) {
    Comment(comment) {
        Column {
            Row {
                TextButton(
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Reply,
                        contentDescription = "",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "回复", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUpOffAlt,
                        contentDescription = "",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${comment.likeCount}", fontSize = 12.sp)
                }
            }
            if (comment.replyCommentList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        comment.replyCommentList.forEach {
                            key(it.id) {
                                ReplayComment(comment)
                            }
                        }
                    }
                }
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
            CommentWithAction(it)
        }
    }
}

@Preview
@Composable
fun TestPreview() {
    TextButton(
        modifier = Modifier.background(Color.Gray),
        contentPadding = PaddingValues(0.dp),
        onClick = {

        }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.Reply,
            contentDescription = "",
            modifier = Modifier.size(14.dp)
        )
        Text(text = "回复", fontSize = 14.sp)
    }
}