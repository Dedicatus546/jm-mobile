package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.par9uet.jm.store.HistorySearchManager
import com.par9uet.jm.ui.components.ComicSearchHistoryTag
import com.par9uet.jm.ui.viewModel.ComicSearchViewModel
import kotlinx.coroutines.flow.drop
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@Composable
fun ComicSearchScreen(
    historySearchManager: HistorySearchManager = getKoin().get(),
    comicSearchViewModel: ComicSearchViewModel = koinViewModel()
) {
    val mainNavController = LocalMainNavController.current
    val focusRequester = remember { FocusRequester() }
    val textFieldState = rememberTextFieldState()
    val historySearchState by historySearchManager.historySearchState.collectAsState()
    val comicSearchResultState by comicSearchViewModel.comicSearchResultState.collectAsState()

    fun onSearch(text: String) {
        comicSearchViewModel.search(text)
    }

    LaunchedEffect(Unit) {
        comicSearchViewModel.comicSearchResultState.drop(1).collect {
            if (it.data != null) {
                val type = it.data.type
                val content = it.data.content
                if ("redirect" == type) {
                    val id = it.data.redirect!!
                    mainNavController.navigate("comicDetail/${id}")
                } else if ("page" == type) {
                    mainNavController.navigate("comicSearchResult/$content")
                }
                comicSearchViewModel.addHistoryItem(content)
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {
                    mainNavController.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }
                Spacer(Modifier.width(8.dp))
                TextField(
                    lineLimits = TextFieldLineLimits.SingleLine,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    state = textFieldState,
                    placeholder = {
                        Text("搜索")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    onKeyboardAction = {
                        onSearch(textFieldState.text.toString())
                    }
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {
                    textFieldState.edit {
                        replace(0, length, "")
                    }
                }) {
                    Icon(Icons.Default.Close, contentDescription = "清除搜索文本")
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    enabled = !comicSearchResultState.isLoading,
                    onClick = {
                        onSearch(textFieldState.text.toString())
                    }
                ) {
                    if (comicSearchResultState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                }
            }
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("搜索历史", fontWeight = FontWeight.ExtraBold)
                    TextButton(
                        enabled = historySearchState.isNotEmpty(),
                        onClick = {
                            historySearchManager.clear()
                        }
                    ) {
                        Text("清空")
                    }
                }
                if (historySearchState.isNotEmpty()) {
                    FlowRow(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        historySearchState.forEach {
                            key(it) {
                                ComicSearchHistoryTag(label = it, onClick = {
                                    onSearch(it)
                                })
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = "空空如也",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                    }
                }
            }
        }
    }
}