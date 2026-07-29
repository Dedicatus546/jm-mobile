package com.par9uet.jm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import com.par9uet.jm.utils.shimmer

@Composable
fun FilterItem(
    enabled: Boolean = true,
    label: String,
    active: Boolean,
    onClick: (() -> Unit) = {}
) {
    Surface(
        enabled = enabled,
        modifier = Modifier.clip(RoundedCornerShape(4.dp)),
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

@Composable
fun FilterItemSkeleton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
            .shimmer(),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            text = "\u00A0",
            fontSize = 14.sp
        )
    }
}