package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.par9uet.jm.data.models.ReadBottomSettingOption
import com.par9uet.jm.ui.compsable.ComicReadEffect
import com.par9uet.jm.ui.provider.LocalReadBottomSettingOption
import com.par9uet.jm.ui.viewModel.LocalComicReadViewModel

private val readBottomSettingOption = ReadBottomSettingOption(
    showShuntSwitch = false
)

@Composable
fun LocalComicReadScreen(
    comicId: Int,
) {
    val localComicReadViewModel: LocalComicReadViewModel = hiltViewModel()

    ComicReadEffect(
        vm = localComicReadViewModel
    )

    CompositionLocalProvider(LocalReadBottomSettingOption provides readBottomSettingOption) {
        ComicReadContainer(
            comicId,
            localComicReadViewModel
        )
    }
}