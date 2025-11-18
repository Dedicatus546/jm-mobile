package com.par9uet.jm.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ComicWorkTag(label: String) {
    AssistChip(
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(255, 253, 231, 255)
        ),
        onClick = {
            // TODO
        },
        label = {
            Text(
                text = label,
            )
        })
}