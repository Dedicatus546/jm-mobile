package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsBar(
    modifier: Modifier = Modifier,
    sliderValue: Float,
    comicReadViewModel: ComicReadViewModel,
    onSliderValueChange: (value: Float) -> Unit
) {
    val size = comicReadViewModel.size
    Card(
        modifier = modifier
            .padding(
                start = 20.dp,
                bottom = 40.dp,
                end = 20.dp
            ),

        ) {
        Slider(
            modifier = Modifier.padding(20.dp),
            value = sliderValue,
            onValueChange = onSliderValueChange,
            steps = max(0, size - 2),
            valueRange = 0f..max(0, size - 1).toFloat()
        )
    }
}