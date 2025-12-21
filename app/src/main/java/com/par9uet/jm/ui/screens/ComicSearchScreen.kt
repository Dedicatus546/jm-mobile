package com.par9uet.jm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.components.ComicSearchHistoryTag

@Composable
fun ComicSearchScreen() {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {

                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }
                Spacer(Modifier.width(8.dp))
                TextField(
                    modifier = Modifier.weight(1f),
                    state = rememberTextFieldState(),
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
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {

                }) {
                    Icon(Icons.Default.Close, contentDescription = "")
                }
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {

                }) {
                    Icon(Icons.Default.Search, contentDescription = "")
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
                    TextButton(onClick = {
                    }) {
                        Text("清空")
                    }
                }
                FlowRow(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in 0 until 8) {
                        key(i) {
                            ComicSearchHistoryTag("test$i")
                        }
                    }
                }
            }
        }
    }
}