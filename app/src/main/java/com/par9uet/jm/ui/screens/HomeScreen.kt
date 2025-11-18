package com.par9uet.jm.ui.screens

import androidx.activity.ComponentActivity
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.par9uet.jm.data.models.HomeComicSwiperItem
import com.par9uet.jm.http.getPromoteComicListApi
import com.par9uet.jm.ui.components.ComicComponent
import com.par9uet.jm.utils.createComic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen() {
    val homeViewModel: HomeViewModel = viewModel(LocalContext.current as ComponentActivity)
    val loading = homeViewModel.loading
    val promoteComicList = homeViewModel.homeComicSwiperItemList
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
                .padding(8.dp)
                .fillMaxWidth(),
        ) {
            itemsIndexed(
                promoteComicList,
                key = { _, item -> item.id }) { _, promoteComic ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(promoteComic.title, modifier = Modifier.padding(bottom = 8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(promoteComic.list, key = { it.id }) { comic ->
                            ComicComponent(
                                comic = comic,
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
    var homeComicSwiperItemList by mutableStateOf(listOf<HomeComicSwiperItem>())
    var loading by mutableStateOf(false)

    fun getPromoteComicList() {
        viewModelScope.launch {
            if (homeComicSwiperItemList.isEmpty()) {
                loading = true
                val data = withContext(Dispatchers.IO) {
                    getPromoteComicListApi()
                }
                homeComicSwiperItemList = data.data.map { it ->
                    HomeComicSwiperItem(
                        id = it.id,
                        title = it.title,
                        list = it.content.map {
                            createComic(
                                id = it.id.toInt(),
                                name = it.name,
                                authorList = listOf(it.author)
                            )
                        }
                    )
                }
                loading = false
            }
        }
    }
}