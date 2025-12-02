package com.par9uet.jm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Option(val label: String, val value: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingSelectDialog(
    title: String,
    value: String?,
    optionList: List<Option> = listOf(),
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
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
            ) {
                optionList.forEach {
                    Row(
                        modifier = Modifier
                            .clickable(onClick = {
                                onSelect(it.value)
                            })
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = it.value === value,
                            onClick = {
                                onSelect(it.value)
                            }
                        )
                        Text(text = it.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

            }
        }
    }
}