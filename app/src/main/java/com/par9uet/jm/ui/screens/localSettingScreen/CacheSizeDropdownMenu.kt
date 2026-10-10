package com.par9uet.jm.ui.screens.localSettingScreen

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.utils.formatFileSize

private val cacheSizeList = listOf(
    1024 * 1024 * 1024L,
    1024 * 1024 * 2048L,
    1024 * 1024 * 4096L,
    1024 * 1024 * 8192L,
)

@Composable
fun CacheSizeDropdownMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    cacheSize: Long,
    onCacheSizeChange: (Long) -> Unit,
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        Text(formatFileSize(cacheSize))
        ExposedDropdownMenu(
            modifier = Modifier.width(200.dp),
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            },
        ) {
            cacheSizeList.forEach {
                DropdownMenuItem(
                    trailingIcon = {
                        if (cacheSize == it) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "选中$it"
                            )
                        }
                    },
                    text = {
                        Text(formatFileSize(it))
                    },
                    onClick = {
                        onCacheSizeChange(it)
                    }
                )
            }
        }
    }
}