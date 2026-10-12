package com.par9uet.jm.ui.screens.readScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.par9uet.jm.data.models.ReadBottomSettingOption
import com.par9uet.jm.ui.compsable.ComicReadEffect
import com.par9uet.jm.ui.provider.LocalLocalSettingManager
import com.par9uet.jm.ui.provider.LocalReadBottomSettingOption
import com.par9uet.jm.ui.viewModel.OnlineComicReadViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.drop

private val readBottomSettingOption = ReadBottomSettingOption(
    showShuntSwitch = true
)

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
fun OnlineComicReadScreen(
    comicId: Int,
) {
    val onlineComicReadViewModel: OnlineComicReadViewModel = hiltViewModel()

    val localSettingManager = LocalLocalSettingManager.current
    val localSetting by localSettingManager.localSettingState.collectAsState()

    LaunchedEffect(Unit) {
        // 网络阅读下独有的切换线路重新加载页面
        snapshotFlow { localSetting.shunt }.drop(1).collect {
            onlineComicReadViewModel.load(comicId)
        }
    }

    ComicReadEffect(
        vm = onlineComicReadViewModel
    )

    CompositionLocalProvider(LocalReadBottomSettingOption provides readBottomSettingOption) {
        ComicReadContainer(
            comicId,
            onlineComicReadViewModel
        )
    }
}