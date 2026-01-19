package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.components.Comic
import com.par9uet.jm.ui.components.CommonScaffold
import com.par9uet.jm.ui.viewModel.ComicDetailViewModel
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun ComicRelateListScreen(
    comicDetailViewModel: ComicDetailViewModel = koinActivityViewModel(),
) {
    val comicDetailState by comicDetailViewModel.comicDetailState.collectAsState()
    CommonScaffold(title = "相关本子") {
        if (comicDetailState.data != null) {
            val relateList = comicDetailState.data!!.relateComicList
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                contentPadding = PaddingValues(10.dp),
            ) {
                items(
                    relateList,
                    key = { it.id },
                ) {
                    Comic(comic = it)
                }
            }
        }
    }
}