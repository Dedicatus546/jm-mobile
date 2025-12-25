package com.par9uet.jm.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterItem(
    label: String,
    active: Boolean,
    onClick: (() -> Unit) = {}
) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)),
        onClick = onClick,
        color = if (active) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            text = label,
            fontSize = 14.sp
        )
    }
}