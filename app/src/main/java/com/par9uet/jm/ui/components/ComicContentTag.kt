package com.par9uet.jm.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.par9uet.jm.ui.screens.LocalMainNavController

@Composable
fun ComicContentTag(label: String) {
    val mainNavController = LocalMainNavController.current
    AssistChip(
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(255, 235, 238, 255)
        ),
        onClick = {
            mainNavController.navigate("comicSearchResult/$label")
        },
        label = {
            Text(
                text = label,
            )
        })
}