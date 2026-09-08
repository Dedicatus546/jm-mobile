package com.par9uet.jm.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SettingListItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconContentDescription: String,
    title: String,
    description: String? = null,
    onClick: () -> Unit = {},
    action: @Composable () -> Unit
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription
            )
        },
        trailingContent = action,
        supportingContent = if (description != null) {
            { Text(description) }
        } else null,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
    }
}