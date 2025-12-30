package com.par9uet.jm.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.par9uet.jm.ui.screens.LocalMainNavController
import com.par9uet.jm.ui.theme.surfaceContainerLight

@Composable
fun ComicSearchHistoryTag(label: String, onClick: () -> Unit = {}) {
    val mainNavController = LocalMainNavController.current
    AssistChip(
        border = null,
        onClick = onClick,
        label = {
            Text(label)
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = surfaceContainerLight
        )
    )
}