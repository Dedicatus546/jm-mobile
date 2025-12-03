package com.par9uet.jm.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.par9uet.jm.ui.screens.LocalMainNavController

@Composable
fun ComicRoleTag(label: String) {
    val mainNavController = LocalMainNavController.current
    AssistChip(
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(224, 247, 250, 255)
        ),
        onClick = {
            mainNavController.navigate("comicQuickSearch/$label")
        },
        label = {
            Text(
                text = label,
            )
        })
}