package com.par9uet.jm.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextOverflow
import com.par9uet.jm.viewModel.UserCollectComicViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCollectComicScreen(
    userCollectComicViewModel: UserCollectComicViewModel = koinViewModel()
) {
    val comicList = userCollectComicViewModel.list
    LaunchedEffect(Unit) {
        userCollectComicViewModel.getCollectComicList(1)
    }
    Scaffold(
        topBar = {
            val scrollBehavior =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "个人中心",
                        color = MaterialTheme.colorScheme.surface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->

    }
}