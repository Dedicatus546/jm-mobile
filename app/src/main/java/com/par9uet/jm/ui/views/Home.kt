package com.par9uet.jm.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.par9uet.jm.data.models.Comic
import com.par9uet.jm.data.models.PromoteComicListItem
import com.par9uet.jm.http.getPromoteComicListApi
import com.par9uet.jm.ui.components.ComicComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Home() {
    val homeViewModel: HomeViewModel = viewModel()
    val loading = homeViewModel.loading
    val promoteComicList = homeViewModel.promoteComicList
    LaunchedEffect(Unit) {
        homeViewModel.getPromoteComicList()
    }
    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            itemsIndexed(
                promoteComicList,
                key = { index, item -> item.id }) { index, promoteComic ->
                if (index > 0) HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(promoteComic.title, modifier = Modifier.padding(bottom = 8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(promoteComic.list, key = { it.id }) { comic ->
                            ComicComponent(
                                id = comic.id,
                                name = comic.name,
                                author = comic.author,
                                modifier = Modifier.width(150.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

class HomeViewModel : ViewModel() {
    var promoteComicList by mutableStateOf(listOf<PromoteComicListItem>())
    var loading by mutableStateOf(false)

    fun getPromoteComicList() {
        viewModelScope.launch {
            loading = true
            val data = withContext(Dispatchers.IO) {
                getPromoteComicListApi()
            }
            promoteComicList = data.data.map {
                PromoteComicListItem(
                    id = it.id,
                    title = it.title,
                    list = it.content.map {
                        Comic(
                            id = it.id.toInt(),
                            name = it.name,
                            author = it.author
                        )
                    }
                )
            }
            loading = false
        }
    }
}