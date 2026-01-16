package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.par9uet.jm.ui.viewModel.ComicReadViewModel
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsBar(
    modifier: Modifier = Modifier,
    comicReadViewModel: ComicReadViewModel
) {
    var currentIndexState by comicReadViewModel.currentIndexState
    val size = comicReadViewModel.size
    val sliderState = rememberSliderState(
        value = currentIndexState.toFloat(),
        steps = size - 2,
        valueRange = 0f..(size - 1).toFloat(),
    )
    LaunchedEffect(sliderState) {
        snapshotFlow { sliderState.isDragging }
            .filter { it }
            .collect {
                currentIndexState = sliderState.value.toInt()
            }
    }
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
            state = sliderState,
        )
    }
}