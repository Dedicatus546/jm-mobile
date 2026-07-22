package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.CommonScaffold

@Composable
fun ComicRelateListScreen(
    relateComicList: List<Comic>
) {
    CommonScaffold(title = "相关本子") {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
            contentPadding = PaddingValues(10.dp),
        ) {
            items(
                relateComicList,
                key = { it.id },
            ) {
                Comic(comic = it)
            }
        }
    }
}