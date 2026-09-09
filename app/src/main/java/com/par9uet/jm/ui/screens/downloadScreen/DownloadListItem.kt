package com.par9uet.jm.ui.screens.downloadScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.par9uet.jm.database.model.DownloadComic
import com.par9uet.jm.ui.components.ComicCoverImage

@Composable
fun DownloadListItem(
    modifier: Modifier = Modifier,
    comic: DownloadComic,
    onClick: () -> Unit = {}
) {
    val textMeasurer = rememberTextMeasurer(cacheSize = 0)
    Card(
        onClick = onClick
    ) {
        Box(modifier = modifier.drawWithContent {
            drawContent()
            when (comic.status) {
                "pending", "downloading" -> {
                    val progress = comic.progress ?: 0f
                    // 如果已完成，不绘制蒙层
                    if (progress >= 1f) {
                        return@drawWithContent
                    }
                    val overlayHeight = size.height * (1f - progress)
                    drawRect(
                        color = Color.Black.copy(alpha = 0.4f),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, overlayHeight)
                    )
                    val text = "${(progress * 100).toInt()}%  "
                    val style = TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            blurRadius = 10f
                        )
                    )
                    val textLayoutResult =
                        textMeasurer.measure(
                            text = text,
                            style = style,
                            constraints = Constraints(
                                maxWidth = Constraints.Infinity
                            ),
                            overflow = TextOverflow.Ellipsis,
                        )
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(
                            x = size.width - textLayoutResult.size.width,
                            y = if (overlayHeight >= textLayoutResult.size.height) overlayHeight - textLayoutResult.size.height else overlayHeight
                        ),
                    )
                }

                "error" -> {}
                else -> {}
            }
        }) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ComicCoverImage(
                    downloadComic = comic
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    text = comic.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp),
                    text = comic.authorList.joinToString(",").ifBlank { "暂无作者" },
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}