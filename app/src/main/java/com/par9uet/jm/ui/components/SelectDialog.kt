package com.par9uet.jm.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SelectOption(val label: String, val value: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDialog(
    title: String,
    value: String?,
    selectOptionList: List<SelectOption> = listOf(),
    onSelect: (String) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.fillMaxHeight(.8f)
            ) {
                item {
                    Box {}
                }
                items(selectOptionList, key = { it.value }) {
                    Row(
                        modifier = Modifier
                            .clickable(onClick = {
                                onSelect(it.value)
                            })
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = it.value === value,
                            onClick = {
                                onSelect(it.value)
                            }
                        )
                        Text(text = it.label)
                    }
                }
                item {
                    Box {}
                }
            }
        }
    }
}