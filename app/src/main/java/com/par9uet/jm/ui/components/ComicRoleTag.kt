package com.par9uet.jm.ui.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ComicRoleTag(label: String) {
    AssistChip(
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(224, 247, 250, 255)
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